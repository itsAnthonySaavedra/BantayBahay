package com.example.bantaybahay.ChangePassword

class ChangePasswordPresenter(
    private val repository: ChangePasswordRepository
) : IChangePasswordPresenter {

    private var view: IChangePasswordView? = null

    override fun attachView(view: IChangePasswordView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        // Validate current password
        if (currentPassword.isEmpty()) {
            view?.onCurrentPasswordError("Current password is required")
            return
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            view?.onNewPasswordError("New password is required")
            return
        }

        if (newPassword.length < 8) {
            view?.onNewPasswordError("Password must be at least 8 characters")
            return
        }

        if (!newPassword.any { it.isUpperCase() }) {
            view?.onNewPasswordError("Password must contain uppercase letter")
            return
        }

        if (!newPassword.any { it.isLowerCase() }) {
            view?.onNewPasswordError("Password must contain lowercase letter")
            return
        }

        if (!newPassword.any { it.isDigit() }) {
            view?.onNewPasswordError("Password must contain a number")
            return
        }

        if (!newPassword.any { !it.isLetterOrDigit() }) {
            view?.onNewPasswordError("Password must contain a special character")
            return
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            view?.onConfirmPasswordError("Please confirm your new password")
            return
        }

        if (newPassword != confirmPassword) {
            view?.onConfirmPasswordError("Passwords do not match")
            return
        }

        // Check if new password is same as current
        if (currentPassword == newPassword) {
            view?.onNewPasswordError("New password must be different from current password")
            return
        }

        // All validations passed, proceed with password change
        view?.showProgress()

        repository.changePassword(
            currentPassword,
            newPassword,
            onSuccess = {
                view?.hideProgress()
                view?.onPasswordChangeSuccess("Password changed successfully! Please login again.")

                // Logout user after successful password change
                repository.logout()

                // Redirect to login
                view?.logoutAndRedirectToLogin()
            },
            onFailure = { errorMessage ->
                view?.hideProgress()
                view?.onPasswordChangeFailed(errorMessage)
            }
        )
    }
}