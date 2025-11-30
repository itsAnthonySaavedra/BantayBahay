package com.example.bantaybahay.ChangePassword
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordActivity : Activity(), IChangePasswordView {

    private lateinit var presenter: IChangePasswordPresenter
    private lateinit var repository: ChangePasswordRepository
    private lateinit var loadingDialog: LoadingDialog

    // UI Elements
    private lateinit var backArrow: ImageView
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tilCurrentPassword: TextInputLayout
    private lateinit var tilNewPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var btnUpdatePassword: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)

        // Initialize repository and presenter
        repository = ChangePasswordRepository()
        presenter = ChangePasswordPresenter(repository)
        presenter.attachView(this)

        // Initialize views
        backArrow = findViewById(R.id.backArrow)
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tilCurrentPassword = findViewById(R.id.tilCurrentPassword)
        tilNewPassword = findViewById(R.id.tilNewPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)

        // Setup click listeners
        backArrow.setOnClickListener { finish() }

        btnUpdatePassword.setOnClickListener {
            // Clear previous errors
            tilCurrentPassword.error = null
            tilNewPassword.error = null
            tilConfirmPassword.error = null

            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            presenter.changePassword(currentPassword, newPassword, confirmPassword)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    // --- IChangePasswordView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Changing password...")
        btnUpdatePassword.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
        btnUpdatePassword.isEnabled = true
    }

    override fun onPasswordChangeSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onPasswordChangeFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onCurrentPasswordError(message: String) {
        tilCurrentPassword.error = message
        etCurrentPassword.requestFocus()
    }

    override fun onNewPasswordError(message: String) {
        tilNewPassword.error = message
        etNewPassword.requestFocus()
    }

    override fun onConfirmPasswordError(message: String) {
        tilConfirmPassword.error = message
        etConfirmPassword.requestFocus()
    }

    override fun logoutAndRedirectToLogin() {
        // Delay for 2 seconds to show success message
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 2000)
    }
}