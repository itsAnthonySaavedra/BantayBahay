package com.example.bantaybahay.Notifications

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class NotificationRepository {

    private val tokenRef = FirebaseDatabase.getInstance().getReference("deviceTokens")

    fun saveDeviceToken(onDone: (Boolean)->Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                tokenRef.child(token).setValue(true)
                onDone(true)
            }
            .addOnFailureListener {
                onDone(false)
            }
    }

}
