package com.example.bantaybahay.Settings

class SettingsPresenter(
    private val repository: SettingsRepository
) : ISettingsPresenter {

    private var view: ISettingsView? = null

    override fun attachView(view: ISettingsView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onDeviceClicked() {
        view?.navigateToDeviceDetails()
    }

    override fun onAddDeviceClicked() {
        view?.navigateToAddDevice()
    }

    override fun onRemoveDeviceClicked() {
        view?.navigateToRemoveDevice()
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onChangePasswordClicked() {
        view?.navigateToChangePassword()
    }

    override fun onLogoutClicked() {
        // Show confirmation dialog first
        view?.showLogoutConfirmation()
    }

    // Called after user confirms logout
    fun confirmLogout() {
        repository.logout(
            onSuccess = {
                view?.performLogout()
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
            }
        )
    }
}