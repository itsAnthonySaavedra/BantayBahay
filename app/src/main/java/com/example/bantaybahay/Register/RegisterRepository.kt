package com.example.bantaybahay.Register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class RegisterRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Validation
        if (fullName.isEmpty()) {
            onFailure("Full name is required")
            return
        }

        if (email.isEmpty()) {
            onFailure("Email is required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onFailure("Please enter a valid email address")
            return
        }

        if (password.isEmpty()) {
            onFailure("Password is required")
            return
        }

        if (password.length < 6) {
            onFailure("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            onFailure("Passwords do not match")
            return
        }

        // Create user with Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Update user profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            // Save to Realtime Database (without phone)
                            saveUserToRealtimeDatabase(user.uid, fullName, email, onSuccess, onFailure, user)
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception.message ?: "Failed to update profile")
                        }
                } else {
                    onFailure("Failed to create user")
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Registration failed")
            }
    }

    private fun saveUserToRealtimeDatabase(
        userId: String,
        fullName: String,
        email: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit,
        user: FirebaseUser
    ) {
        // Save basic user data (phone will be added later in Details)
        val userMap = hashMapOf(
            "email" to email,
            "displayName" to fullName,
            "createdAt" to System.currentTimeMillis()
        )

        database.reference
            .child("users")
            .child(userId)
            .setValue(userMap)
            .addOnSuccessListener {
                onSuccess(user)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to save user data")
            }
    }
}