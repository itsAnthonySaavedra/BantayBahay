package com.example.bantaybahay.Register
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.bantaybahay.Detail.DetailsActivity
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : Activity(), IRegisterView {

    // MVP Components
    private lateinit var presenter: IRegisterPresenter
    private lateinit var repository: RegisterRepository

    // Loading Dialog
    private lateinit var loadingDialog: LoadingDialog

    // UI Elements
    private lateinit var ivLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var etFullname: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var cbAgreeTerms: CheckBox
    private lateinit var btnRegister: AppCompatButton
    private lateinit var tvSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        // Initialize MVP components
        repository = RegisterRepository()
        presenter = RegisterPresenter(repository)
        presenter.attachView(this)

        // Initialize views
        ivLogo = findViewById(R.id.ivLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        etFullname = findViewById(R.id.etFullname)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbAgreeTerms = findViewById(R.id.cbAgreeTerms)
        btnRegister = findViewById(R.id.btnRegister)
        tvSignIn = findViewById(R.id.tvSignIn)

        // Register button click
        btnRegister.setOnClickListener {
            // Clear previous errors
            etFullname.error = null
            etEmail.error = null
            etPassword.error = null
            etConfirmPassword.error = null

            val fullname = etFullname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val isAgreed = cbAgreeTerms.isChecked

            presenter.register(fullname, email, password, confirmPassword, isAgreed)
        }

        // Sign in text click
        tvSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    // --- IRegisterView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Creating your account...")
        btnRegister.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
        btnRegister.isEnabled = true
    }

    override fun onRegisterSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        val intent = Intent(this, DetailsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRegisterFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onFullnameError(message: String) {
        etFullname.error = message
    }

    override fun onEmailError(message: String) {
        etEmail.error = message
    }

    override fun onPasswordError(message: String) {
        etPassword.error = message
    }

    override fun onConfirmPasswordError(message: String) {
        etConfirmPassword.error = message
    }

    override fun onAgreementError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}