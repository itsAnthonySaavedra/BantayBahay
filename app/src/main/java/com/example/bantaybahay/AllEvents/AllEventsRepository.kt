package com.example.bantaybahay.AllEvents

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AllEventsRepository {
    private val database = FirebaseDatabase.getInstance()
    private val devicesRef = database.getReference("devices")

    interface LogsListener {
        // Timestamp, Message, DeviceName
        fun onLogsLoaded(logs: List<Triple<String, String, String>>)
        fun onError(message: String)
    }

    fun getAllLogs(listener: LogsListener) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            listener.onError("User not logged in")
            return
        }

        // Query all devices owned by this user
        devicesRef.orderByChild("owner_uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        listener.onLogsLoaded(emptyList())
                        return
                    }

                    val allLogs = mutableListOf<Triple<String, String, String>>()

                    for (deviceSnapshot in snapshot.children) {
                        // FIX: Use "name" key to match SettingsRepository schema. Fallback to deviceId if unnamed.
                        val deviceName = deviceSnapshot.child("name").getValue(String::class.java) ?: deviceSnapshot.key ?: "Unknown Device"
                        val logsSnapshot = deviceSnapshot.child("logs")

                        for (log in logsSnapshot.children) {
                            val timestamp = log.key ?: continue // e.g., 2023-10-27_10-30-00
                            val message = log.getValue(String::class.java) ?: continue
                            
                            allLogs.add(Triple(timestamp, message, deviceName))
                        }
                    }

                    // Sort: Newest first (descending timestamp string works well for ISO-like dates)
                    allLogs.sortByDescending { it.first }

                    listener.onLogsLoaded(allLogs)
                }

                override fun onCancelled(error: DatabaseError) {
                    listener.onError(error.message)
                }
            })
    }

    fun clearAllLogs() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        devicesRef.orderByChild("owner_uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (device in snapshot.children) {
                            // Remove the "logs" node for each device
                            device.ref.child("logs").removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Log error if needed
                }
            })
    }
}