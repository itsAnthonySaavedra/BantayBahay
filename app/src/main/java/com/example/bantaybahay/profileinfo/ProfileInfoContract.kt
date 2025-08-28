package com.example.bantaybahay.profileinfo

interface ProfileInfoContract {
    interface View {
        fun navigateToHomeScreen()
        fun showLoading()
        fun hideLoading()
        fun showSaveError(message: String)
    }

    interface Presenter {
        fun onSignUpClicked(phone: String?, location: String?)
        fun onSkipClicked()
        fun onAddPhotoClicked()
        fun onDestroy()
    }
}