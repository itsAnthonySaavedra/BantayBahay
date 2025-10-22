package com.example.bantaybahay.Detail
import android.net.Uri

class DetailsPresenter(
    private val repository: DetailsRepository
) : IDetailsPresenter {

    private var view: IDetailsView? = null
    private var selectedPhotoUri: Uri? = null

    override fun attachView(view: IDetailsView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onPhotoSelected(uri: Uri) {
        selectedPhotoUri = uri
        view?.displayPhotoPreview(uri.toString())
        view?.enableNextButton()
    }

    override fun onNextClicked(phoneNumber: String, location: String) {
        view?.showProgress()
        view?.disableNextButton()

        if (selectedPhotoUri != null) {
            // Upload photo first
            repository.uploadProfilePhoto(
                selectedPhotoUri!!,
                onSuccess = { photoUrl ->
                    // Photo uploaded, now save profile
                    saveProfile(phoneNumber, location, photoUrl)
                },
                onFailure = { errorMessage ->
                    view?.showPhotoUploadError(errorMessage)
                    view?.hideProgress()
                    view?.enableNextButton()
                }
            )
        } else {
            // No photo selected, just save profile
            saveProfile(phoneNumber, location, null)
        }
    }

    override fun onSkipClicked() {
        // Skip profile setup and go to onboarding
        view?.navigateToOnboarding()
    }

    private fun saveProfile(phoneNumber: String, location: String, photoUrl: String?) {
        val profile = UserProfile(
            phoneNumber = phoneNumber.ifEmpty { null },
            location = location.ifEmpty { null },
            photoUrl = photoUrl
        )

        repository.saveUserProfile(
            profile,
            onSuccess = {
                // Update Firebase Auth profile if photo exists
                if (photoUrl != null) {
                    repository.updateAuthProfile(
                        photoUrl,
                        onSuccess = {
                            view?.hideProgress()
                            view?.navigateToOnboarding()
                        },
                        onFailure = { errorMessage ->
                            // Profile saved but auth update failed - still proceed
                            view?.hideProgress()
                            view?.navigateToOnboarding()
                        }
                    )
                } else {
                    view?.hideProgress()
                    view?.navigateToOnboarding()
                }
            },
            onFailure = { errorMessage ->
                view?.hideProgress()
                view?.showValidationError(errorMessage)
                view?.enableNextButton()
            }
        )
    }
}