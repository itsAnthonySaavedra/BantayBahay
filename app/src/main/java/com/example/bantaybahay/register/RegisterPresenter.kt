package com.example.bantaybahay.register

import android.util.Patterns
import java.util.regex.Pattern

class RegisterPresenter(
    private var view: RegisterContract.View?
) : RegisterContract.Presenter {

    override fun onSignUpClicked(
        fullName: String,
        email: String,
        phone: String,
        pass: String,
        confirmPass: String
    ) {
        var isValid = true

        // Validate Full Name
        if (fullName.isBlank()) {
            view?.showFullNameError("Full Name cannot be empty")
            isValid = false
        }

        // Validate Email
        if (email.isBlank()) {
            view?.showEmailError("Email cannot be empty")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showEmailError("Please enter a valid email address")
            isValid = false
        }

        // Validate Phone Number
        if (phone.isBlank()) {
            view?.showPhoneNumberError("Phone Number cannot be empty")
            isValid = false
        } else if (!isValidPhilippinePhoneNumber(phone)) {
            view?.showPhoneNumberError("Please enter a valid PH phone number (e.g., 09171234567)")
            isValid = false
        }

        // Validate Password
        if (pass.isBlank()) {
            view?.showPasswordError("Password cannot be empty")
            isValid = false
        } else if (pass.length < 6) {
            view?.showPasswordError("Password must be at least 6 characters long")
            isValid = false
        } else if (!containsSpecialCharacter(pass)) {
            view?.showPasswordError("Password must contain at least one special character")
            isValid = false
        }

        // Validate Confirm Password
        if (confirmPass.isBlank()) {
            view?.showConfirmPasswordError("Please confirm your password")
            isValid = false
        } else if (pass != confirmPass) {
            view?.showConfirmPasswordError("Passwords do not match")
            isValid = false
        }

        if (isValid) {
            view?.showRegistrationSuccess()
            view?.navigateToNextScreen()
        }
    }

    private fun isValidPhilippinePhoneNumber(phone: String): Boolean {
        val regex = "^(09|\\+639)\\d{9}$"
        return Pattern.compile(regex).matcher(phone).matches()
    }

    private fun containsSpecialCharacter(password: String): Boolean {
        val specialCharPattern = Pattern.compile("[^A-Za-z0-9]")
        return specialCharPattern.matcher(password).find()
    }

    override fun onDestroy() {
        // Destroy view
        view = null
    }
}
