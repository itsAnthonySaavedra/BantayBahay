package com.example.bantaybahay.SplashScreen

interface ISplashPresenter {
    fun attachView(view: ISplashView)
    fun detachView()
    fun checkUserAuthentication()
}