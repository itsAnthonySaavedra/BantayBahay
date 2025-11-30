package com.example.bantaybahay.ForgotPassword

interface IForgotPasswordPresenter {
    fun attachView(view: IForgotPasswordView)
    fun detachView()
    fun sendPasswordResetEmail(email: String)
}