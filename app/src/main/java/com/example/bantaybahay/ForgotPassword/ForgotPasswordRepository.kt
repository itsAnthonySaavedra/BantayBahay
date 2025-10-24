package com.example.bantaybahay.ForgotPassword

import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordRepository {

    private val auth = FirebaseAuth.getInstance()

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to send reset email")
            }
    }
}