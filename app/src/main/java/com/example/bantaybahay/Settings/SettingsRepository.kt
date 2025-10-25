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
    val status: String
)

class SettingsRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    fun getUserDevices(onSuccess: (List<Device>) -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in.")

        database.child("devices").orderByChild("owner_uid").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val deviceList = mutableListOf<Device>()
                    snapshot.children.forEach { deviceSnapshot ->
                        val deviceId = deviceSnapshot.key ?: ""
                        val deviceName = deviceSnapshot.child("name").getValue(String::class.java) ?: deviceId
                        val deviceStatus = deviceSnapshot.child("status").getValue(String::class.java) ?: "offline"
                        deviceList.add(Device(deviceId, deviceName, deviceStatus))
                    }
                    onSuccess(deviceList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }
            })
    }

    fun renameDevice(deviceId: String, newName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("devices").child(deviceId).child("name").setValue(newName)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to rename device.") }
    }

    fun removeDevice(deviceId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("devices").child(deviceId).removeValue()
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