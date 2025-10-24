package com.example.bantaybahay.SplashScreen

class SplashPresenter(
    private val repository: SplashRepository
) : ISplashPresenter {

    private var view: ISplashView? = null

    override fun attachView(view: ISplashView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun checkUserAuthentication() {
        if (repository.isUserLoggedIn()) {
            view?.navigateToDashboard()
        } else {
            view?.navigateToLogin()
        }
    }
}