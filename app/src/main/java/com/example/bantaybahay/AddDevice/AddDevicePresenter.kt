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
        view?.showLoading("Starting...")
        // DEBUG: Show toast
        (view as? android.content.Context)?.let {
            android.widget.Toast.makeText(it, "Step 1: Requesting Token (Internet)...", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Ensure we are NOT bound to WiFi (so we can use Mobile Data for Internet)
        view?.bindNetworkToWifi(false)

        // Step 1 — Get custom token from Firebase
        repository.requestCustomToken(deviceId) { ok, tokenOrError ->
            if (!ok || tokenOrError == null) {
                view?.hideLoading()
                view?.showClaimingError("Token Failed: $tokenOrError")
                return@requestCustomToken
            }

            val token = tokenOrError
            // DEBUG: Show toast
            (view as? android.content.Context)?.let {
                android.widget.Toast.makeText(it, "Step 2: Sending to ESP (WiFi)...", android.widget.Toast.LENGTH_SHORT).show()
            }

            // Step 2 — Send WiFi credentials + token to ESP32 AP
            // NOW we bind to WiFi to reach 192.168.4.1
            view?.bindNetworkToWifi(true)

            // Give a delay for binding to take effect
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                repository.sendCredentialsToEsp(deviceId, token, ssid, password) { success, errorMessage ->
                    view?.hideLoading()
                    // Unbind after done
                    view?.bindNetworkToWifi(false)

                    if (success) {
                        // Step 3: Claim the device in Firebase (Link to User)
                        repository.claimDevice(deviceId) { claimed, claimErr ->
                            if (claimed) {
                                view?.onDeviceClaimed(deviceId)
                            } else {
                                view?.showClaimingError("Paired but failed to save to DB: $claimErr")
                            }
                        }
                    } else {
                        view?.showClaimingError("Failed to send data: $errorMessage")
                    }
                }
            }, 4000)
        }
    }
}
