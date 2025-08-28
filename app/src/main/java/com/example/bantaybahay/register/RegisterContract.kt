package com.example.bantaybahay.register

interface RegisterContract {

    interface View {
        fun showFullNameError(message: String)
        fun showEmailError(message: String)
        fun showPhoneNumberError(message: String)
        fun showPasswordError(message: String)
        fun showConfirmPasswordError(message: String)
        fun clearErrors()
        fun showProgressBar()
        fun hideProgressBar()
        fun navigateToNextScreen()
        fun showRegistrationSuccess()
        fun showRegistrationError(message: String)
    }

    interface Presenter {
        fun onSignUpClicked(
            fullName: String,
            email: String,
            phone: String,
            pass: String,
            confirmPass: String
        )
        // Call this method when presenter is not needed.
        fun onDestroy()
    }
}
