package com.example.bantaybahay.Detail
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

data class UserProfile(
    val phoneNumber: String?,
    val location: String?,
    val photoUrl: String?
)

class DetailsRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // 1. Upload profile photo to Firebase Storage
    fun uploadProfilePhoto(
        photoUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            onFailure("User not authenticated")
            return
        }

        val storageRef = storage.reference
        val photoRef = storageRef.child("profile_photos/$userId.jpg")

        photoRef.putFile(photoUri)
            .addOnSuccessListener {
                // Get the download URL
                photoRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to get download URL")
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to upload photo")
            }
    }

    // 2. Save user profile details to Firebase Realtime Database
    fun saveUserProfile(
        profile: UserProfile,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            onFailure("User not authenticated")
            return
        }

        val userUpdates = mutableMapOf<String, Any?>()

        // Only add non-null values
        profile.phoneNumber?.let { userUpdates["phone"] = it }
        profile.location?.let { userUpdates["location"] = it }
        profile.photoUrl?.let { userUpdates["photoUrl"] = it }

        // Add profile complete flag
        userUpdates["profileComplete"] = true
        userUpdates["updatedAt"] = System.currentTimeMillis()

        database.reference
            .child("users")
            .child(userId)
            .updateChildren(userUpdates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to save profile")
            }
    }

    // 3. Update Firebase Auth user profile (for photoUrl)
    fun updateAuthProfile(
        photoUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onFailure("User not authenticated")
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(photoUrl))
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update auth profile")
            }
    }
}