package com.example.bantaybahay.ForgotPassword
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : Activity(), IForgotPasswordView {

    private lateinit var presenter: IForgotPasswordPresenter
    private lateinit var repository: ForgotPasswordRepository
    private lateinit var loadingDialog: LoadingDialog

    // UI Elements
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var btnSendReset: AppCompatButton
    private lateinit var tvBackToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)

        // Initialize repository and presenter
        repository = ForgotPasswordRepository()
        presenter = ForgotPasswordPresenter(repository)
        presenter.attachView(this)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        tilEmail = findViewById(R.id.tilEmail)
        btnSendReset = findViewById(R.id.btnSendReset)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        // Setup click listeners
        btnSendReset.setOnClickListener {
            // Clear previous errors
            tilEmail.error = null

            val email = etEmail.text.toString().trim()
            presenter.sendPasswordResetEmail(email)
        }

        tvBackToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    // --- IForgotPasswordView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Sending reset email...")
        btnSendReset.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
        btnSendReset.isEnabled = true
    }

    override fun onResetEmailSent(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        // Navigate back to login after successful email sent
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, 2000)
    }

    override fun onResetEmailFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onEmailError(message: String) {
        tilEmail.error = message
        etEmail.requestFocus()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}