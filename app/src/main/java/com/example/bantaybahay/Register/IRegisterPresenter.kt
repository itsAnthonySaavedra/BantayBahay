package com.example.bantaybahay.Register

interface IRegisterPresenter {
    fun attachView(view: IRegisterView)
    fun detachView()
    fun register(
        fullname: String,
        email: String,
        password: String,
        confirmPassword: String,
        isAgreed: Boolean
    )
}
