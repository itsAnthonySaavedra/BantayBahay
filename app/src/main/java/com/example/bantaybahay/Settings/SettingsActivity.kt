package com.example.bantaybahay.Settings
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bantaybahay.ChangePassword.ChangePasswordActivity
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.Profile.ProfileActivity
import com.example.bantaybahay.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : Activity(), ISettingsView {

    // MVP Components
    private lateinit var presenter: ISettingsPresenter
    private lateinit var repository: SettingsRepository

    // UI Elements
    private lateinit var deviceRow: ConstraintLayout
    private lateinit var addDeviceRow: ConstraintLayout
    private lateinit var removeDeviceRow: ConstraintLayout
    private lateinit var profileRow: ConstraintLayout
    private lateinit var changePasswordRow: ConstraintLayout
    private lateinit var logoutRow: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize MVP components
        repository = SettingsRepository(this)
        presenter = SettingsPresenter(repository)
        presenter.attachView(this)

        // Initialize UI elements
        deviceRow = findViewById(R.id.deviceRow)
        addDeviceRow = findViewById(R.id.addDeviceRow)
        removeDeviceRow = findViewById(R.id.removeDeviceRow)
        profileRow = findViewById(R.id.profileRow)
        changePasswordRow = findViewById(R.id.changePasswordRow)
        logoutRow = findViewById(R.id.logoutRow)

        // Set up click listeners
        deviceRow.setOnClickListener { presenter.onDeviceClicked() }
        addDeviceRow.setOnClickListener { presenter.onAddDeviceClicked() }
        removeDeviceRow.setOnClickListener { presenter.onRemoveDeviceClicked() }
        profileRow.setOnClickListener { presenter.onProfileClicked() }
        changePasswordRow.setOnClickListener { presenter.onChangePasswordClicked() }
        logoutRow.setOnClickListener { presenter.onLogoutClicked() }

        // Setup bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_settings

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_activity -> {
                    // Navigate to Activity screen if you have one
                    true
                }
                R.id.navigation_settings -> {
                    // Already here
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- ISettingsView Implementation ---

    override fun navigateToDeviceDetails() {
        // Navigate to device details screen
        Toast.makeText(this, "Navigate to Device Details", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, DeviceDetailsActivity::class.java))
    }

    override fun navigateToAddDevice() {
        // Navigate to add device screen
        Toast.makeText(this, "Navigate to Add Device", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, AddDeviceActivity::class.java))
    }

    override fun navigateToRemoveDevice() {
        // Navigate to remove device screen
        Toast.makeText(this, "Navigate to Remove Device", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, RemoveDeviceActivity::class.java))
    }

    override fun navigateToProfile() {
        // Navigate to profile screen
        Toast.makeText(this, "Navigate to Profile", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToChangePassword() {
        // Navigate to change password screen
        Toast.makeText(this, "Navigate to Change Password", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }

    override fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                (presenter as SettingsPresenter).confirmLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun performLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}