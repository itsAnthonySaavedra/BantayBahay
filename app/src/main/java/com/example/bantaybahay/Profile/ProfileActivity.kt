package com.example.bantaybahay.Profile
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.util.Locale

class ProfileActivity : Activity(), IProfileView {

    private lateinit var presenter: IProfilePresenter
    private lateinit var repository: ProfileRepository
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // UI Elements
    private lateinit var backArrow: ImageView
    private lateinit var profilePhoto: CircleImageView
    private lateinit var btnEditPhoto: Button
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var tilLocation: TextInputLayout
    private lateinit var fabSave: ExtendedFloatingActionButton

    private val PICK_IMAGE_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize components
        loadingDialog = LoadingDialog(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        repository = ProfileRepository(this)
        presenter = ProfilePresenter(repository)
        presenter.attachView(this)

        // Initialize views
        backArrow = findViewById(R.id.backArrow)
        profilePhoto = findViewById(R.id.profilePhoto)
        btnEditPhoto = findViewById(R.id.btnEditPhoto)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etLocation = findViewById(R.id.etLocation)
        tilLocation = findViewById(R.id.tilLocation)
        fabSave = findViewById(R.id.fabSave)

        // Setup click listeners
        backArrow.setOnClickListener { finish() }

        profilePhoto.setOnClickListener { openImagePicker() }
        btnEditPhoto.setOnClickListener { openImagePicker() }

        // GPS location icon click
        tilLocation.setEndIconOnClickListener {
            getCurrentLocation()
        }

        // Save button click
        fabSave.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val location = etLocation.text.toString().trim()

            presenter.onSaveClicked(name, phone, location)

            // Hide keyboard and clear focus
            hideKeyboardAndClearFocus()
        }

        // Show Save button when user starts editing
        setupTextWatchers()

        // Load user profile
        presenter.loadUserProfile()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showSaveButton()
            }
        }

        etFullName.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)
        etLocation.addTextChangedListener(textWatcher)
    }

    private fun showSaveButton() {
        if (fabSave.visibility != View.VISIBLE) {
            fabSave.visibility = View.VISIBLE
            fabSave.show()
        }
    }

    private fun hideSaveButton() {
        fabSave.hide()
        fabSave.visibility = View.GONE
    }

    private fun hideKeyboardAndClearFocus() {
        // Clear focus from all EditTexts
        etFullName.clearFocus()
        etPhone.clearFocus()
        etLocation.clearFocus()

        // Hide keyboard
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // Hide save button after saving
        hideSaveButton()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        loadingDialog.show("Getting your location...")

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                loadingDialog.dismiss()

                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        if (addresses?.isNotEmpty() == true) {
                            val address = addresses[0]
                            val addressParts = mutableListOf<String>()

                            address.locality?.let { addressParts.add(it) }
                            address.adminArea?.let { addressParts.add(it) }

                            val addressText = addressParts.joinToString(", ")
                            etLocation.setText(addressText)

                            Toast.makeText(this, "Location detected!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                loadingDialog.dismiss()
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                profilePhoto.setImageURI(imageUri)
                presenter.onPhotoSelected(imageUri)
                showSaveButton() // Show save button after photo selected
            }
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
                getCurrentLocation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        presenter.detachView()
    }

    // --- IProfileView Implementation ---

    override fun showProgress() {
        loadingDialog.show("Updating profile...")
        fabSave.isEnabled = false
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
        fabSave.isEnabled = true
    }

    override fun displayUserProfile(
        name: String,
        email: String,
        phone: String?,
        location: String?,
        photoUrl: String?
    ) {
        etFullName.setText(name)
        etEmail.setText(email)
        etPhone.setText(phone ?: "")
        etLocation.setText(location ?: "")

        // Load circular profile photo
        photoUrl?.let {
            if (it.startsWith("http")) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profilePhoto)
            }
        }
    }

    override fun showUpdateSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateBack() {
        finish()
    }
}