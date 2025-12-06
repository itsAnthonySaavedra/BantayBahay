package com.example.bantaybahay.Settings
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Device(
    val id: String,
    val name: String,
    val status: String,
    val lastSeen: Long = 0
)

class SettingsRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    fun getUserDevices(onSuccess: (List<Device>) -> Unit, onFailure: (String) -> Unit): ValueEventListener {
        val userId = auth.currentUser?.uid ?: run {
            onFailure("User not logged in.")
            return object : ValueEventListener { override fun onDataChange(s: DataSnapshot) {}; override fun onCancelled(e: DatabaseError) {} }
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val deviceList = mutableListOf<Device>()
                snapshot.children.forEach { deviceSnapshot ->
                    val deviceId = deviceSnapshot.key ?: ""
                    val deviceName = deviceSnapshot.child("name").getValue(String::class.java) ?: deviceId
                    val deviceStatus = deviceSnapshot.child("status").getValue(String::class.java) ?: "offline"
                    val lastSeen = deviceSnapshot.child("last_seen").getValue(Long::class.java) ?: 0L
                    
                    deviceList.add(Device(deviceId, deviceName, deviceStatus, lastSeen))
                }
                onSuccess(deviceList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        }
        
        database.child("devices").orderByChild("owner_uid").equalTo(userId).addValueEventListener(listener)
        return listener
    }

    fun renameDevice(deviceId: String, newName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("devices").child(deviceId).child("name").setValue(newName)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to rename device.") }
    }

    fun removeDevice(deviceId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // 1. Remove owner_uid (Unclaim)
        database.child("devices").child(deviceId).child("owner_uid").removeValue()
        
        // 2. Clear name/logs if desired, OR just leave them for next user.
        // For security, checking "RESET" command in firmware is better.
        
        // 3. Send RESET command to device (if online)
        database.child("devices").child(deviceId).child("door_command").setValue("RESET")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to remove device.") }
    }

    fun logout(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            FirebaseAuth.getInstance().signOut()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Logout failed")
        }
    }
}