package com.example.bantaybahay.Profile
import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val location: String? = null,
    val photoUrl: String? = null
)

class ProfileRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun getUserProfile(
        onSuccess: (UserProfile) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure("User not logged in")
            return
        }

        database.reference
            .child("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = UserProfile(
                        name = snapshot.child("displayName").getValue(String::class.java) ?: "",
                        email = snapshot.child("email").getValue(String::class.java) ?: "",
                        phone = snapshot.child("phone").getValue(String::class.java),
                        location = snapshot.child("location").getValue(String::class.java),
                        photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
                    )
                    onSuccess(profile)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }
            })
    }

    fun uploadProfilePhoto(
        photoUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure("User not authenticated")
            return
        }

        val storageRef = storage.reference.child("profile_photos/$userId.jpg")

        storageRef.putFile(photoUri)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to get download URL")
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to upload photo")
            }
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        location: String,
        photoUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure("User not authenticated")
            return
        }

        val updates = mutableMapOf<String, Any>(
            "displayName" to name,
            "updatedAt" to System.currentTimeMillis()
        )

        // Add optional fields if not empty
        if (phone.isNotEmpty()) {
            updates["phone"] = phone
        }
        if (location.isNotEmpty()) {
            updates["location"] = location
        }
        photoUrl?.let { updates["photoUrl"] = it }

        // Update database
        database.reference
            .child("users")
            .child(userId)
            .updateChildren(updates)
            .addOnSuccessListener {
                // Update Firebase Auth display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .apply {
                        photoUrl?.let { setPhotoUri(Uri.parse(it)) }
                    }
                    .build()

                auth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to update auth profile")
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update profile")
            }
    }
}