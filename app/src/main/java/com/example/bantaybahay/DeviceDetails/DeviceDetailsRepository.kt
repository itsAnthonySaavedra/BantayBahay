package com.example.bantaybahay.DeviceDetails

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class DeviceDetails(
    val name: String,
    val id: String,
    val status: String,
    val armStatus: String // Added arm status
)

class DeviceDetailsRepository {
    private val database = Firebase.database.reference

    fun getDeviceDetails(deviceId: String, callback: (DeviceDetails?) -> Unit) {
        database.child("devices").child(deviceId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java) ?: deviceId
                        val status = snapshot.child("status").getValue(String::class.java) ?: "Offline"
                        val armCommand = snapshot.child("door_command").getValue(String::class.java) ?: "DISARM"
                        val armStatus = if (armCommand == "ARM") "Armed" else "Disarmed"

                        callback(DeviceDetails(name, deviceId, status, armStatus))
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun renameDevice(deviceId: String, newName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("devices").child(deviceId).child("name").setValue(newName)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Rename failed.") }
    }
}