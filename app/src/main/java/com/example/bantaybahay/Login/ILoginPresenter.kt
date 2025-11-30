package com.example.bantaybahay.Login

interface ILoginPresenter {
    fun attachView(view: ILoginView)
    fun detachView()
    fun login(email: String, password: String)
}