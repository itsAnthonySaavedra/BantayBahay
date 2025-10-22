package com.example.bantaybahay.Detail
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.bantaybahay.Onboarding.Onboarding1Activity
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class DetailsActivity : Activity(), IDetailsView {

    // MVP Components
    private lateinit var presenter: IDetailsPresenter
    private lateinit var repository: DetailsRepository

    // Loading Dialog
    private lateinit var loadingDialog: LoadingDialog

    // Location Client
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // UI Elements
    private lateinit var backArrow: ImageView
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var locationEditText: TextInputEditText
    private lateinit var locationInputLayout: TextInputLayout
    private lateinit var profilePhotoPlaceholder: ImageView
    private lateinit var signUpButton: Button
    private lateinit var skipButton: TextView

    private val PICK_IMAGE_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize MVP components
        repository = DetailsRepository()
        presenter = DetailsPresenter(repository)
        presenter.attachView(this)

        // Initialize UI elements
        backArrow = findViewById(R.id.backArrow)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        locationInputLayout = findViewById(R.id.locationInputLayout)
        profilePhotoPlaceholder = findViewById(R.id.profilePhotoPlaceholder)
        signUpButton = findViewById(R.id.signUpButton)
        skipButton = findViewById(R.id.skipButton)

        // Set up click listeners
        backArrow.setOnClickListener {
            finish()
        }

        profilePhotoPlaceholder.setOnClickListener {
            openImagePicker()
        }

        // Make the location end icon clickable
        locationInputLayout.setEndIconOnClickListener {
            getCurrentLocation()
        }

        signUpButton.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()
            presenter.onNextClicked(phone, location)
        }

        skipButton.setOnClickListener {
            presenter.onSkipClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun getCurrentLocation() {
        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        // Show loading
        loadingDialog.show("Getting your location...")

        // Get last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                loadingDialog.dismiss()

                if (location != null) {
                    // Convert coordinates to address
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        if (addresses?.isNotEmpty() == true) {
                            val address = addresses[0]

                            // Format: "City, Province, Country"
                            val addressParts = mutableListOf<String>()

                            address.locality?.let { addressParts.add(it) } // City
                            address.adminArea?.let { addressParts.add(it) } // Province/State

                            val addressText = addressParts.joinToString(", ")
                            locationEditText.setText(addressText)

                            Toast.makeText(this, "Location detected!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Location not available. Please enable GPS.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                loadingDialog.dismiss()
                Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required to use this feature",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                presenter.onPhotoSelected(imageUri)
            }
        }
    }

    // --- IDetailsView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Saving profile...")
        signUpButton.isEnabled = false
        skipButton.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
    }

    override fun displayPhotoPreview(uri: String) {
        profilePhotoPlaceholder.setImageURI(Uri.parse(uri))
    }

    override fun showValidationError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToOnboarding() {
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Onboarding1Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showPhotoUploadError(message: String) {
        Toast.makeText(this, "Photo upload failed: $message", Toast.LENGTH_SHORT).show()
    }

    override fun enableNextButton() {
        signUpButton.isEnabled = true
        skipButton.isEnabled = true
    }

    override fun disableNextButton() {
        signUpButton.isEnabled = false
        skipButton.isEnabled = false
    }
}