package com.example.bantaybahay.ChangePassword

interface IChangePasswordView {
    fun showProgress()
    fun hideProgress()
    fun onPasswordChangeSuccess(message: String)
    fun onPasswordChangeFailed(message: String)
    fun onCurrentPasswordError(message: String)
    fun onNewPasswordError(message: String)
    fun onConfirmPasswordError(message: String)
    fun logoutAndRedirectToLogin()
}