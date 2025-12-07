package com.example.bantaybahay.DeviceDetails

class DeviceDetailsPresenter(
    private val repository: DeviceDetailsRepository,
    private val deviceId: String
) {
    private var view: IDeviceDetailsView? = null // FIXED: This variable is restored
    private var currentDetails: DeviceDetails? = null

    fun attachView(view: IDeviceDetailsView) {
        this.view = view
        loadDeviceDetails()
    }

    fun detachView() {
        view = null
    }

    private fun loadDeviceDetails() {
        view?.showLoading()
        repository.getDeviceDetails(deviceId) { details ->
            view?.hideLoading()
            if (details != null) {
                currentDetails = details
                view?.displayDeviceDetails(
                    details.name, 
                    details.id, 
                    details.status, 
                    details.armStatus == "Armed",
                    details.autoArmEnabled,
                    details.autoArmTime
                )
            } else {
                view?.showError("Failed to load device details.")
                view?.closeScreen()
            }
        }
    }

    fun onSaveClicked(newName: String) {
        view?.showLoading()
        repository.renameDevice(deviceId, newName,
            onSuccess = {
                view?.hideLoading()
                view?.showSaveSuccess("Device renamed successfully.")
            },
            onFailure = { error ->
                view?.hideLoading()
                view?.showError(error)
            }
        )
    }

    fun onArmToggled(isArmed: Boolean) {
        // Prevent toggling if offline or state mismatch
        if (currentDetails?.status != "Online") {
            // view?.showError("Device is offline.") // Optional: strict mode
            // For now allow, but maybe warn? The Activity has onOfflineWarning logic but that's for removal.
        }
        
        view?.showLoading()
        repository.setArmStatus(deviceId, isArmed,
            onSuccess = {
                view?.hideLoading()
                view?.showSaveSuccess("Arm status updated.")
            },
            onFailure = { error ->
                view?.hideLoading()
                view?.showError(error)
                // Revert switch in UI if needed, but displayDeviceDetails updates via LiveData usually
            }
        )
    }
    
    fun onAutoArmToggled(isChecked: Boolean) {
        val currentTime = currentDetails?.autoArmTime ?: "--:--"
        saveAutoArmSettings(isChecked, currentTime)
    }
    
    fun onAutoArmTimeClicked() {
        val currentTime = currentDetails?.autoArmTime ?: "--:--"
        view?.showTimePicker(currentTime)
    }
    
    fun onTimeSelected(hour: Int, minute: Int) {
        val formattedTime = String.format("%02d:%02d", hour, minute)
        // Enable Auto-Arm when time is set, if not already
        val isEnabled = true 
        saveAutoArmSettings(isEnabled, formattedTime)
    }
    
    private fun saveAutoArmSettings(enabled: Boolean, time: String) {
        view?.showLoading()
        repository.saveAutoArmSettings(deviceId, enabled, time,
            onSuccess = {
                view?.hideLoading()
                view?.showSaveSuccess("Auto-Arm settings saved.")
                 // Update local state optimistically or wait for invalidation
                 // Since getDeviceDetails is real-time value listener, it should update automatically.
            },
            onFailure = { error ->
                view?.hideLoading()
                view?.showError(error)
            }
        )
    }
}