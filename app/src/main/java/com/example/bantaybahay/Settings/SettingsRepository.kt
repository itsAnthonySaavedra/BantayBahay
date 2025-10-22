package com.example.bantaybahay.Settings
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

class SettingsRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BantayBahay_Prefs", Context.MODE_PRIVATE)

    // Logout user
    fun logout(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            // Clear SharedPreferences
            sharedPreferences.edit().clear().apply()

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Logout failed")
        }
    }

    // Get user profile data
    fun getUserProfile(onSuccess: (String, String) -> Unit, onFailure: (String) -> Unit) {
        try {
            // Fetch user data from Firebase or SharedPreferences
            val displayName = sharedPreferences.getString("user_name", "User") ?: "User"
            val email = sharedPreferences.getString("user_email", "user@example.com") ?: "user@example.com"

            onSuccess(displayName, email)
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to load profile")
        }
    }
}