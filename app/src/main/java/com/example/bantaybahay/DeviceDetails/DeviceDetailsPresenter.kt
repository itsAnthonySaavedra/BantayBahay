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
                view?.displayDeviceDetails(details.name, details.id, details.status, details.armStatus)
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
}