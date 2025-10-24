package com.example.bantaybahay.AddDevice

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddDevicePresenter(private val repository: AddDeviceRepository) {

    private var view: IAddDeviceView? = null
    private val unclaimedDevicesRef = FirebaseDatabase.getInstance().getReference("unclaimed_devices")
    private var listener: ValueEventListener? = null

    fun attachView(view: IAddDeviceView) {
        this.view = view
    }

    fun detachView() {
        if (listener != null) {
            unclaimedDevicesRef.removeEventListener(listener!!)
        }
        this.view = null
    }

    fun startDeviceSearch() {
        view?.showLoading("Searching for new device...\n(Power on the ESP32 now)")

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    val deviceNode = snapshot.children.first()
                    val deviceId = deviceNode.key
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (deviceId != null && deviceId != "placeholder" && currentUser != null) {
                        // Stop listening once we find a device
                        unclaimedDevicesRef.removeEventListener(listener!!)
                        claimDevice(deviceId, currentUser.uid)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                view?.hideLoading()
                view?.showClaimingError(error.message)
            }
        }
        unclaimedDevicesRef.addValueEventListener(listener!!)
    }

    private fun claimDevice(deviceId: String, userId: String) {
        view?.showLoading("Claiming device '$deviceId'...")

        repository.claimDevice(deviceId, userId,
            onSuccess = {
                view?.hideLoading()
                view?.onDeviceClaimed(deviceId)
            },
            onFailure = { errorMessage ->
                view?.hideLoading()
                view?.showClaimingError(errorMessage)
            }
        )
    }
}