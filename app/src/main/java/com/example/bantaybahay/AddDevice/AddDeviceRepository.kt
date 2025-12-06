package com.example.bantaybahay.AddDevice

import com.google.firebase.functions.FirebaseFunctions
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AddDeviceRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    /**
     * STEP 1
     * Request a Firebase Custom Token for the ESP32 using the Cloud Function
     */
    fun requestCustomToken(
        deviceId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Specify the region to match your Database URL (asia-southeast1)
        FirebaseFunctions.getInstance("asia-southeast1")
            .getHttpsCallable("issueCustomToken")
            .call(mapOf("deviceId" to deviceId))
            .addOnSuccessListener { result ->
                val map = result.data as Map<*, *>
                val token = map["token"] as String
                callback(true, token)
            }
            .addOnFailureListener { error ->
                callback(false, error.message ?: "Unknown Firebase error")
            }
    }

    /**
     * STEP 2
     * Send WiFi SSID + Password + Custom Token to ESP32 AP endpoint (/pair)
     */
    fun sendCredentialsToEsp(
        deviceId: String,
        token: String,
        ssid: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {

        val json = """
            {
              "deviceId": "$deviceId",
              "token": "$token",
              "ssid": "$ssid",
              "password": "$password"
            }
        """.trimIndent()
        
        println("DEBUG: Sending Token Length: ${token.length}")
        println("DEBUG: Token Start: ${token.take(10)}... End: ${token.takeLast(10)}")

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.4.1/pair")  // Endpoint hosted by ESP32 AP
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "ESP32 returned error: ${response.code}")
                }
            }
        })
    }

    fun claimDevice(deviceId: String, callback: (Boolean, String?) -> Unit) {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            callback(false, "User not logged in")
            return
        }

        val dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("devices").child(deviceId)
        val deviceData = mapOf(
            "owner_uid" to uid,
            "status" to "online",
            "door_command" to "DISARM",
            "name" to "New Device",
            "door_status" to "Closed"
        )

        dbRef.updateChildren(deviceData)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}
