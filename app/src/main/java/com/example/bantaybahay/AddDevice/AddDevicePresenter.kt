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
        listener?.let {
            unclaimedDevicesRef.removeEventListener(it)
        }
        this.view = null
    }

    fun startDeviceSearch() {
        view?.showLoading("Searching for unclaimed device in Firebase...")

        // This listener looks for any device in the "unclaimed_devices" node.
        val searchListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Find the first device that isn't the placeholder
                    val deviceNode = snapshot.children.firstOrNull { it.key != "placeholder" }
                    val deviceId = deviceNode?.key
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (deviceId != null && currentUser != null) {
                        claimDevice(deviceId, currentUser.uid)
                    } else {
                        view?.hideLoading()
                        view?.showClaimingError("No new unclaimed devices found. Please add one manually to Firebase.")
                    }
                } else {
                    view?.hideLoading()
                    view?.showClaimingError("No unclaimed devices found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                view?.hideLoading()
                view?.showClaimingError(error.message)
            }
        }
        // Use addListenerForSingleValueEvent to search just once when the button is clicked.
        unclaimedDevicesRef.addListenerForSingleValueEvent(searchListener)
    }

    private fun claimDevice(deviceId: String, userId: String) {
        view?.showLoading("Found device '$deviceId'. Claiming...")

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