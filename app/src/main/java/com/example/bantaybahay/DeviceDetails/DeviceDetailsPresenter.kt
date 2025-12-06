package com.example.bantaybahay.DeviceDetails

class DeviceDetailsPresenter(
    private val repository: DeviceDetailsRepository,
    private val deviceId: String
) {
    private var view: IDeviceDetailsView? = null

    fun attachView(view: IDeviceDetailsView) {
        this.view = view
        loadDeviceDetails()
    }

    fun detachView() {
        this.view = null
    }

    private fun loadDeviceDetails() {
        view?.showLoading()
        repository.getDeviceDetails(deviceId) { details ->
            view?.hideLoading()
            if (details != null) {
                val isArmed = details.armStatus == "Armed"
                view?.displayDeviceDetails(details.name, details.id, details.status, isArmed)
                
                // Validation: Check Heartbeat
                val currentTime = System.currentTimeMillis() / 1000
                val isOffline = (currentTime - details.lastSeen) > 30 
                view?.showOfflineWarning(isOffline)
            } else {
                view?.showError("Device not found.")
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

    fun onArmToggled(isChecked: Boolean) {
        // Optimistic update not needed as we have real-time listener, but nice for UI responsiveness
        // view?.showLoading() // Optional, might be annoying on a switch
        repository.setArmStatus(deviceId, isChecked,
            onSuccess = { 
                // Success - Realtime listener will update the switch
            },
            onFailure = { error ->
                view?.showError(error)
                // Revert switch if failed? For now, real-time listener will correct it eventually
            }
        )
    }
}