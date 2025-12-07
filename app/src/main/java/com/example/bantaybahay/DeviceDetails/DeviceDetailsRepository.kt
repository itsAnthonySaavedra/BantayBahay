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
    val armStatus: String,
    val lastSeen: Long = 0,
    val autoArmEnabled: Boolean = false,
    val autoArmTime: String = "--:--"
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
                        val lastSeen = snapshot.child("last_seen").getValue(Long::class.java) ?: 0L
                        
                        // Auto-Arm Settings
                        val autoArmEnabled = snapshot.child("settings").child("auto_arm").child("enabled").getValue(Boolean::class.java) ?: false
                        val autoArmTime = snapshot.child("settings").child("auto_arm").child("time").getValue(String::class.java) ?: "--:--"

                        callback(DeviceDetails(name, deviceId, status, armStatus, lastSeen, autoArmEnabled, autoArmTime))
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

    fun setArmStatus(deviceId: String, isArmed: Boolean, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val command = if (isArmed) "ARM" else "DISARM"
        database.child("devices").child(deviceId).child("door_command").setValue(command)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to update arm status.") }
    }
    
    fun saveAutoArmSettings(deviceId: String, enabled: Boolean, time: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val updates = mapOf(
            "devices/$deviceId/settings/auto_arm/enabled" to enabled,
            "devices/$deviceId/settings/auto_arm/time" to time
        )
        
        database.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save auto-arm settings.") }
    }
}