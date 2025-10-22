package com.example.bantaybahay.ChangePassword

interface IChangePasswordPresenter {
    fun attachView(view: IChangePasswordView)
    fun detachView()
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String)
}