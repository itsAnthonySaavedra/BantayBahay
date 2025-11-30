package com.example.bantaybahay.Onboarding

interface IOnboardingPresenter {
    fun attachView(view: IOnboardingView)
    fun detachView()
    fun onNextClicked(currentScreen: Int)
    fun onSkipClicked()
    fun onGetStartedClicked()
}
