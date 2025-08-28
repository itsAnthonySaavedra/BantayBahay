package com.example.bantaybahay.profileinfo

class ProfileInfoPresenter(
    private var view: ProfileInfoContract.View?
) : ProfileInfoContract.Presenter {

    override fun onSignUpClicked(phone: String?, location: String?) {
        view?.navigateToHomeScreen()
    }

    override fun onSkipClicked() {
        view?.navigateToHomeScreen()
    }

    override fun onAddPhotoClicked() {
    }

    override fun onDestroy() {
        view = null
    }
}