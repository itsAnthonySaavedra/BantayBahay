package com.example.bantaybahay.AllEvents

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AllEventsRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    suspend fun getAllUserEvents(onResult: (List<TitledEvent>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(emptyList())
        val allEvents = mutableListOf<TitledEvent>()

        try {
            // 1. Get all devices for the current user
            val devicesSnapshot = database.child("devices").orderByChild("owner_uid").equalTo(userId).get().await()
            if (!devicesSnapshot.exists()) return onResult(emptyList())

            // 2. For each device, get its events
            for (deviceSnapshot in devicesSnapshot.children) {
                val deviceId = deviceSnapshot.key ?: continue
                val deviceName = deviceSnapshot.child("name").getValue(String::class.java) ?: deviceId

                val eventsSnapshot = database.child("events").child(deviceId).get().await()
                for (eventData in eventsSnapshot.children) {
                    val type = eventData.child("type").getValue(String::class.java) ?: "Unknown"
                    val timestamp = eventData.child("timestamp").getValue(String::class.java) ?: ""
                    allEvents.add(TitledEvent(deviceName, type, timestamp))
                }
            }

            // Note: For a real app, you should sort by a numerical timestamp.
            // For this simulation, we'll just return the list as is.
            onResult(allEvents)
        } catch (e: Exception) {
            onResult(emptyList())
        }
    }
}