package com.example.bantaybahay.AddDevice

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDeviceRepository {

    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun claimDevice(deviceId: String, userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val newDeviceRef = rootRef.child("devices/$deviceId")
        val deviceData = mapOf(
            "owner_uid" to userId,
            "door_command" to "DISARM",
            "status" to "online"
        )

        // Set the data for the new device
        newDeviceRef.setValue(deviceData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If successful, remove it from the unclaimed list
                rootRef.child("unclaimed_devices/$deviceId").removeValue()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Failed to remove from unclaimed.") }
            } else {
                onFailure(task.exception?.message ?: "Failed to set new device data.")
            }
        }
    }
}