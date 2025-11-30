package com.example.bantaybahay.Login

interface ILoginView {
    fun showProgress()
    fun hideProgress()
    fun onLoginSuccess(message: String)
    fun onLoginFailed(message: String)
}