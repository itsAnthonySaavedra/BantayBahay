package com.example.bantaybahay.AddDevice

class AddDevicePresenter(
    private val repository: AddDeviceRepository
) {

    private var view: IAddDeviceView? = null

    fun attachView(v: IAddDeviceView) {
        view = v
    }

    fun detachView() {
        view = null
    }

    fun pairDevice(deviceId: String, ssid: String, password: String) {
        view?.showLoading("Pairing device...")

        // Step 1 — Get custom token from Firebase
        repository.requestCustomToken(deviceId) { ok, tokenOrError ->
            if (!ok || tokenOrError == null) {
                view?.hideLoading()
                view?.showClaimingError("Failed to get token: $tokenOrError")
                return@requestCustomToken
            }

            val token = tokenOrError

            // Step 2 — Send WiFi credentials + token to ESP32 AP
            repository.sendCredentialsToEsp(deviceId, token, ssid, password) { success ->
                view?.hideLoading()

                if (success) {
                    view?.onDeviceClaimed(deviceId)
                } else {
                    view?.showClaimingError("Failed to send data to ESP32")
                }
            }
        }
    }
}
