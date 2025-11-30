package com.example.bantaybahay.Detail

interface IDetailsView {
    fun showProgress()
    fun hideProgress()
    fun displayPhotoPreview(uri: String)
    fun showValidationError(message: String)
    fun navigateToOnboarding()
    fun showPhotoUploadError(message: String)
    fun enableNextButton()
    fun disableNextButton()
}
