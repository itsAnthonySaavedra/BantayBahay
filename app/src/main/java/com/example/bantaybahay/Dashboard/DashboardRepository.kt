package com.example.bantaybahay.Dashboard

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Event(val type: String, val timestamp: String)

class DashboardRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    // Helper function to find the device ID owned by the current user
    private suspend fun getUsersDevice(): String? {
        val userId = auth.currentUser?.uid ?: return null
        val devicesRef = database.child("devices")

        return try {
            val snapshot = devicesRef.orderByChild("owner_uid").equalTo(userId).limitToFirst(1).get().await()
            if (snapshot.exists() && snapshot.hasChildren()) {
                snapshot.children.first().key // This is the deviceId
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // This now listens for real-time status changes from Firebase
    fun getDeviceStatus(onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        auth.currentUser?.uid?.let { userId ->
            database.child("devices").orderByChild("owner_uid").equalTo(userId).limitToFirst(1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            val device = snapshot.children.first()
                            val command = device.child("door_command").getValue(String::class.java)
                            val status = if (command == "ARM") "System Armed" else "System Disarmed"
                            onSuccess(status)
                        } else {
                            onSuccess("No Device Found") // Handles case where user has no device
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFailure(error.message)
                    }
                })
        }
    }

    // This fetches the last 10 events for the user's device
    fun getRecentEvents(onSuccess: (List<Event>) -> Unit, onFailure: (String) -> Unit) {
        auth.currentUser?.uid?.let { userId ->
            database.child("devices").orderByChild("owner_uid").equalTo(userId).limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            val deviceId = snapshot.children.first().key
                            if (deviceId != null) {
                                database.child("events").child(deviceId)
                                    .limitToLast(10) // Get the last 10 events
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(eventSnapshot: DataSnapshot) {
                                            val eventList = mutableListOf<Event>()
                                            for (child in eventSnapshot.children) {
                                                val type = child.child("type").getValue(String::class.java) ?: "Unknown Event"
                                                // Assuming timestamp is stored as a formatted string
                                                val time = child.child("timestamp").getValue(String::class.java) ?: ""
                                                eventList.add(Event(type, time))
                                            }
                                            onSuccess(eventList.reversed()) // Show newest first
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            onFailure(error.message)
                                        }
                                    })
                            } else {
                                onSuccess(emptyList())
                            }
                        } else {
                            onSuccess(emptyList()) // No devices found
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        onFailure(error.message)
                    }
                })
        }
    }

    // Sends the "ARM" command to Firebase
    suspend fun sendArmCommand(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val deviceId = getUsersDevice()
        if (deviceId != null) {
            database.child("devices").child(deviceId).child("door_command").setValue("ARM")
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Failed to arm device") }
        } else {
            onFailure("No device found for this user.")
        }
    }

    // Sends the "DISARM" command to Firebase
    suspend fun sendDisarmCommand(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val deviceId = getUsersDevice()
        if (deviceId != null) {
            database.child("devices").child(deviceId).child("door_command").setValue("DISARM")
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Failed to disarm device") }
        } else {
            onFailure("No device found for this user.")
        }
    }
}