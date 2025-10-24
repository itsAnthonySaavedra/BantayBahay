package com.example.bantaybahay.Register

interface IRegisterView {
    fun showProgress()
    fun hideProgress()
    fun onRegisterSuccess(message: String)
    fun onRegisterFailed(message: String)
    fun onFullnameError(message: String)
    fun onEmailError(message: String)
    fun onPasswordError(message: String)
    fun onConfirmPasswordError(message: String)
    fun onAgreementError(message: String)
}