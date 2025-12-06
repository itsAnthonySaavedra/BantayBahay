package com.example.bantaybahay.Dashboard

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardRepository {

    private val database = FirebaseDatabase.getInstance()
    private val devicesRef = database.getReference("devices")

    interface SensorListener {
        fun onStatusChanged(status: String)
        fun onLogsUpdated(logs: Map<String, String>)
        fun onArmedChanged(isArmed: Boolean)
        fun onError(message: String)
    }

    private var currentDeviceId: String? = null

    fun listenToSensorData(listener: SensorListener) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 1. Find the device owned by this user
        devicesRef.orderByChild("owner_uid").equalTo(uid).limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        listener.onError("No device found for this user.")
                        return
                    }

                    // Get the first device ID
                    for (child in snapshot.children) {
                        currentDeviceId = child.key
                        startListeningToDevice(currentDeviceId!!, listener)
                        return
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    listener.onError(error.message)
                }
            })
    }

    private fun startListeningToDevice(deviceId: String, listener: SensorListener) {
        val deviceRef = devicesRef.child(deviceId)

        // 🔹 Listen for Sensor STATUS (door_status)
        deviceRef.child("door_status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: "Unknown"
                listener.onStatusChanged(status)
            }
            override fun onCancelled(error: DatabaseError) { listener.onError(error.message) }
        })

        // 🔹 Listen for LAST 10 LOGS
        deviceRef.child("logs")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val logs = mutableMapOf<String, String>()
                    for (log in snapshot.children) {
                        val key = log.key ?: continue
                        val value = log.getValue(String::class.java) ?: continue
                        logs[key] = value
                    }
                    listener.onLogsUpdated(logs)
                }
                override fun onCancelled(error: DatabaseError) { listener.onError(error.message) }
            })

        // 🔹 Listen for ARM/DISARM state (door_command) -> Check if "ARM"
        deviceRef.child("door_command").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cmd = snapshot.getValue(String::class.java) ?: "DISARM"
                val isArmed = (cmd == "ARM")
                listener.onArmedChanged(isArmed)
            }
            override fun onCancelled(error: DatabaseError) { listener.onError(error.message) }
        })
    }

    // 🔹 Presenter calls this to update armed state for ALL devices
    fun setArmed(isArmed: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cmd = if (isArmed) "ARM" else "DISARM"

        // Query all devices owned by this user
        devicesRef.orderByChild("owner_uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (device in snapshot.children) {
                            device.ref.child("door_command").setValue(cmd)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // silently fail or log
                }
            })
    }
}
