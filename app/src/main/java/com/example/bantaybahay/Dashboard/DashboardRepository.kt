package com.example.bantaybahay.Dashboard

import com.google.firebase.database.*

class DashboardRepository {

    private val database = FirebaseDatabase.getInstance()

    // Reed sensor parent node
    private val sensorRef = database.getReference("reedSensor")

    // Logs node (limit to last 10)
    private val logsRef = database.getReference("reedSensor/logs")

    // Armed state node
    private val armedRef = database.getReference("reedSensor/armed")

    interface SensorListener {
        fun onStatusChanged(status: String)
        fun onLogsUpdated(logs: Map<String, String>)
        fun onArmedChanged(isArmed: Boolean)
        fun onError(message: String)
    }

    fun listenToSensorData(listener: SensorListener) {

        // 🔹 Listen for Sensor STATUS
        sensorRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: "Unknown"
                listener.onStatusChanged(status)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onError(error.message)
            }
        })

        // 🔹 Listen only to LAST 10 LOGS
        logsRef
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

                override fun onCancelled(error: DatabaseError) {
                    listener.onError(error.message)
                }
            })

        // 🔹 Listen for ARM/DISARM state
        armedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isArmed = snapshot.getValue(Boolean::class.java) ?: false
                listener.onArmedChanged(isArmed)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onError(error.message)
            }
        })

        // 🔥 DEBUG (optional, safe)
        logsRef.get().addOnSuccessListener {
            println("🔥 DEBUG: Logs Found = ${it.childrenCount}")
            for (c in it.children) {
                println("KEY: ${c.key}, VALUE: ${c.value}")
            }
        }
    }

    // 🔹 Presenter calls this to update armed state
    fun setArmed(isArmed: Boolean) {
        armedRef.setValue(isArmed)
    }
}
