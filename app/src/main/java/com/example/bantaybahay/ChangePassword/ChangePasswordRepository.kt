package com.example.bantaybahay.ChangePassword

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordRepository {

    private val auth = FirebaseAuth.getInstance()

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onFailure("User not authenticated")
            return
        }

        val email = user.email
        if (email == null) {
            onFailure("User email not found")
            return
        }

        // Re-authenticate user with current password
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // Re-authentication successful, now update password
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to update password")
                    }
            }
            .addOnFailureListener { exception ->
                // Re-authentication failed (wrong current password)
                onFailure("Current password is incorrect")
            }
    }

    // NEW: Logout function
    fun logout() {
        auth.signOut()
    }
}