package com.example.bantaybahay.Profile
import android.net.Uri

class ProfilePresenter(
    private val repository: ProfileRepository
) : IProfilePresenter {

    private var view: IProfileView? = null
    private var selectedPhotoUri: Uri? = null
    private var currentPhotoUrl: String? = null

    override fun attachView(view: IProfileView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadUserProfile() {
        view?.showProgress()

        repository.getUserProfile(
            onSuccess = { profile ->
                view?.hideProgress()
                currentPhotoUrl = profile.photoUrl
                view?.displayUserProfile(
                    profile.name,
                    profile.email,
                    profile.phone,
                    profile.location,
                    profile.photoUrl
                )
            },
            onFailure = { error ->
                view?.hideProgress()
                view?.showError(error)
            }
        )
    }

    override fun onPhotoSelected(uri: Uri) {
        selectedPhotoUri = uri
        // Preview will be handled in Activity
    }

    override fun onSaveClicked(name: String, phone: String, location: String) {
        if (name.isEmpty()) {
            view?.showError("Name is required")
            return
        }

        view?.showProgress()

        if (selectedPhotoUri != null) {
            // Upload new photo first
            repository.uploadProfilePhoto(
                selectedPhotoUri!!,
                onSuccess = { photoUrl ->
                    updateProfile(name, phone, location, photoUrl)
                },
                onFailure = { error ->
                    view?.hideProgress()
                    view?.showError(error)
                }
            )
        } else {
            // Update without new photo
            updateProfile(name, phone, location, currentPhotoUrl)
        }
    }

    override fun onLocationIconClicked() {
        // This will be handled in Activity (GPS functionality)
    }

    private fun updateProfile(name: String, phone: String, location: String, photoUrl: String?) {
        repository.updateUserProfile(
            name, phone, location, photoUrl,
            onSuccess = {
                view?.hideProgress()
                view?.showUpdateSuccess("Profile updated successfully!")
                view?.navigateBack()
            },
            onFailure = { error ->
                view?.hideProgress()
                view?.showError(error)
            }
        )
    }
}