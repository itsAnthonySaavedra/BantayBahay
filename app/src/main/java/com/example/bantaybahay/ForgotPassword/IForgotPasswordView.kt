package com.example.bantaybahay.ForgotPassword

interface IForgotPasswordView {
    fun showProgress()
    fun hideProgress()
    fun onResetEmailSent(message: String)
    fun onResetEmailFailed(message: String)
    fun onEmailError(message: String)
    fun navigateToLogin()
}