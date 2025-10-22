package com.example.bantaybahay.Onboarding

class OnboardingPresenter(
    private val repository: OnboardingRepository
) : IOnboardingPresenter {

    private var view: IOnboardingView? = null

    override fun attachView(view: IOnboardingView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onNextClicked(currentScreen: Int) {
        when (currentScreen) {
            1 -> view?.navigateToOnboarding2()
            2 -> view?.navigateToOnboarding3()
            3 -> onGetStartedClicked()
        }
    }

    override fun onSkipClicked() {
        // User skipped onboarding, mark as complete and go to dashboard
        repository.markOnboardingComplete(
            onSuccess = {
                view?.navigateToDashboard()
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
            }
        )
    }

    override fun onGetStartedClicked() {
        // User completed all onboarding screens
        repository.markOnboardingComplete(
            onSuccess = {
                view?.navigateToDashboard()
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
            }
        )
    }
}