package com.example.bantaybahay.Login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.ForgotPassword.ForgotPasswordActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Register.RegisterActivity
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity(), ILoginView {

    // MVP Components
    private lateinit var presenter: ILoginPresenter
    private lateinit var repository: UserRepository

    // Loading Dialog
    private lateinit var loadingDialog: LoadingDialog

    // UI Elements
    private lateinit var ivLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        // Initialize MVP components
        repository = UserRepository()
        presenter = LoginPresenter(repository)
        presenter.attachView(this)

        // Initialize views
        ivLogo = findViewById(R.id.ivLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)

        // Login button click
        btnLogin.setOnClickListener {
            // Clear previous errors
            etEmail.error = null
            etPassword.error = null

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            presenter.login(email, password)
        }

        // Forgot Password click
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Register click
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    // --- ILoginView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Signing in...")
        btnLogin.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
        btnLogin.isEnabled = true
    }

    override fun onLoginSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onLoginFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}