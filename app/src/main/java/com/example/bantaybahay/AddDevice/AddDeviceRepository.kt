package com.example.bantaybahay.AddDevice

import com.google.firebase.functions.FirebaseFunctions
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AddDeviceRepository {

    private val client = OkHttpClient()

    /**
     * STEP 1
     * Request a Firebase Custom Token for the ESP32 using the Cloud Function
     */
    fun requestCustomToken(
        deviceId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("issueCustomToken")
            .call(mapOf("deviceId" to deviceId))
            .addOnSuccessListener { result ->
                val map = result.data as Map<*, *>
                val token = map["token"] as String
                callback(true, token)
            }
            .addOnFailureListener { error ->
                callback(false, error.message)
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
        callback: (Boolean) -> Unit
    ) {

        val json = """
            {
              "deviceId": "$deviceId",
              "token": "$token",
              "ssid": "$ssid",
              "password": "$password"
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.4.1/pair")  // Endpoint hosted by ESP32 AP
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }
}
