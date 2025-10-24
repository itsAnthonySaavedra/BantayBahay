package com.example.bantaybahay.ForgotPassword
import android.util.Patterns

class ForgotPasswordPresenter(
    private val repository: ForgotPasswordRepository
) : IForgotPasswordPresenter {

    private var view: IForgotPasswordView? = null

    override fun attachView(view: IForgotPasswordView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun sendPasswordResetEmail(email: String) {
        // Validate email
        if (email.isEmpty()) {
            view?.onEmailError("Email is required")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.onEmailError("Please enter a valid email address")
            return
        }

        // Send reset email
        view?.showProgress()

        repository.sendPasswordResetEmail(
            email,
            onSuccess = {
                view?.hideProgress()
                view?.onResetEmailSent("Password reset email sent! Please check your inbox.")
            },
            onFailure = { errorMessage ->
                view?.hideProgress()

                // Handle specific Firebase errors
                val message = when {
                    errorMessage.contains("no user record", ignoreCase = true) ->
                        "No account found with this email address"
                    errorMessage.contains("network", ignoreCase = true) ->
                        "Network error. Please check your internet connection"
                    else -> errorMessage
                }

                view?.onResetEmailFailed(message)
            }
        )
    }
}