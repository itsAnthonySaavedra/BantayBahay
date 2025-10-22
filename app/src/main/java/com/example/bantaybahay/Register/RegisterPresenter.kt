package com.example.bantaybahay.Register

class RegisterPresenter(
    private val repository: RegisterRepository
) : IRegisterPresenter {

    private var view: IRegisterView? = null

    override fun attachView(view: IRegisterView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun register(
        fullname: String,
        email: String,
        password: String,
        confirmPassword: String,
        isAgreed: Boolean
    ) {
        // Validate fullname
        if (fullname.isEmpty()) {
            view?.onFullnameError("Full name is required")
            return
        }

        if (fullname.length < 2) {
            view?.onFullnameError("Name must be at least 2 characters")
            return
        }

        // Validate email
        if (email.isEmpty()) {
            view?.onEmailError("Email is required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.onEmailError("Please enter a valid email address")
            return
        }

        // Validate password
        if (password.isEmpty()) {
            view?.onPasswordError("Password is required")
            return
        }

        if (password.length < 6) {
            view?.onPasswordError("Password must be at least 6 characters")
            return
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            view?.onConfirmPasswordError("Please confirm your password")
            return
        }

        if (password != confirmPassword) {
            view?.onConfirmPasswordError("Passwords do not match")
            return
        }

        // Check if terms are agreed
        if (!isAgreed) {
            view?.onAgreementError("Please agree to the Terms and Conditions")
            return
        }

        // All validations passed, proceed with registration
        view?.showProgress()

        repository.registerUser(
            fullName = fullname,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            onSuccess = { user ->
                view?.hideProgress()
                view?.onRegisterSuccess("Account created successfully!")
            },
            onFailure = { errorMessage ->
                view?.hideProgress()
                view?.onRegisterFailed(errorMessage)
            }
        )
    }
}