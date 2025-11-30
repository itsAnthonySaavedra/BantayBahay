package com.example.bantaybahay.AllEvents

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllEventsRepository {
    private val database = FirebaseDatabase.getInstance()
    private val logsRef = database.getReference("reedSensor/logs")

    interface LogsListener {
        fun onLogsLoaded(logs: Map<String, String>)
        fun onError(message: String)
    }

    fun getAllLogs(listener: LogsListener) {
        logsRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val logs = mutableMapOf<String, String>()

                for (child in snapshot.children) {
                    val key = child.key ?: continue
                    val value = child.getValue(String::class.java) ?: continue
                    logs[key] = value
                }

                listener.onLogsLoaded(logs)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onError(error.message)
            }
        })
    }
}