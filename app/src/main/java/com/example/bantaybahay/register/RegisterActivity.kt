package com.example.bantaybahay.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bantaybahay.databinding.ActivityRegisterBinding
import com.example.bantaybahay.profileinfo.ProfileInfoActivity

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = RegisterPresenter(this)

        binding.signUpButton.setOnClickListener {
            clearErrors()
            val fullName = binding.fullNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            presenter.onSignUpClicked(fullName, email, phone, password, confirmPassword)
        }
    }

    override fun showFullNameError(message: String) {
        binding.fullNameEditText.error = message
    }

    override fun showEmailError(message: String) {
        binding.emailEditText.error = message
    }

    override fun showPhoneNumberError(message: String) {
        binding.phoneEditText.error = message
    }

    override fun showPasswordError(message: String) {
        binding.passwordEditText.error = message
    }

    override fun showConfirmPasswordError(message: String) {
        binding.confirmPasswordEditText.error = message
    }

    override fun clearErrors() {
        binding.fullNameEditText.error = null
        binding.emailEditText.error = null
        binding.phoneEditText.error = null
        binding.passwordEditText.error = null
        binding.confirmPasswordEditText.error = null
    }

    override fun showProgressBar() {}
    override fun hideProgressBar() {}

    override fun navigateToNextScreen() {
        val intent = Intent(this, ProfileInfoActivity::class.java)
        startActivity(intent)
    }

    override fun showRegistrationSuccess() {
        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_LONG).show()
    }

    override fun showRegistrationError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}