package com.example.bantaybahay.Onboarding
import android.content.Context
import android.content.SharedPreferences

class OnboardingRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BantayBahay_Prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    // Mark onboarding as completed
    fun markOnboardingComplete(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            sharedPreferences.edit()
                .putBoolean(KEY_ONBOARDING_COMPLETE, true)
                .apply()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to save onboarding status")
        }
    }

    // Check if onboarding is already completed
    fun isOnboardingComplete(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }
}