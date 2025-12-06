package com.example.bantaybahay.Settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.AddDevice.AddDeviceActivity
import com.example.bantaybahay.AllEvents.AllEventsActivity
import com.example.bantaybahay.ChangePassword.ChangePasswordActivity
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.DeviceDetails.DeviceDetailsActivity
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.Profile.ProfileActivity
import com.example.bantaybahay.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : Activity(), ISettingsView {

    private lateinit var presenter: SettingsPresenter
    private lateinit var repository: SettingsRepository
    private lateinit var addDeviceRow: ConstraintLayout
    private lateinit var removeDeviceRow: ConstraintLayout
    private lateinit var profileRow: ConstraintLayout
    private lateinit var changePasswordRow: ConstraintLayout
    private lateinit var logoutRow: ConstraintLayout
    private lateinit var devicesRecyclerView: RecyclerView
    private lateinit var tvNoDevices: View
    private lateinit var deviceAdapter: DeviceListAdapter
    private var deviceList: List<Device> = emptyList()

    private var devicesListener: com.google.firebase.database.ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        repository = SettingsRepository(this)
        presenter = SettingsPresenter(repository)
        presenter.attachView(this)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchUserDevices()
    }

    override fun onPause() {
        super.onPause()
        devicesListener?.let {
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("devices")
                    .orderByChild("owner_uid").equalTo(userId).removeEventListener(it)
            }
        }
        devicesListener = null
    }

    private fun initializeViews() {
        addDeviceRow = findViewById(R.id.addDeviceRow)
        removeDeviceRow = findViewById(R.id.removeDeviceRow)
        profileRow = findViewById(R.id.profileRow)
        changePasswordRow = findViewById(R.id.changePasswordRow)
        logoutRow = findViewById(R.id.logoutRow)
        devicesRecyclerView = findViewById(R.id.devicesRecyclerView)
        tvNoDevices = findViewById(R.id.tvNoDevices)
    }

    private fun setupClickListeners() {
        addDeviceRow.setOnClickListener { presenter.onAddDeviceClicked() }
        removeDeviceRow.setOnClickListener { showRemoveDeviceDialog() }
        profileRow.setOnClickListener { presenter.onProfileClicked() }
        changePasswordRow.setOnClickListener { presenter.onChangePasswordClicked() }
        logoutRow.setOnClickListener { presenter.onLogoutClicked() }
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceListAdapter(
            onDeviceClick = { device -> navigateToDeviceDetails(device) },
            onDeviceLongClick = { device -> showRenameDialog(device) }
        )
        devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        devicesRecyclerView.adapter = deviceAdapter
    }

    private fun fetchUserDevices() {
        // Only fetch if not already listening
        if (devicesListener != null) return

        devicesListener = repository.getUserDevices(
            onSuccess = { devices ->
                this.deviceList = devices
                if (devices.isNotEmpty()) {
                    devicesRecyclerView.visibility = View.VISIBLE
                    tvNoDevices.visibility = View.GONE
                    deviceAdapter.updateDevices(devices)
                } else {
                    devicesRecyclerView.visibility = View.GONE
                    tvNoDevices.visibility = View.VISIBLE
                }
            },
            onFailure = { error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show() }
        )
    }

    private fun showRenameDialog(device: Device) {
        val editText = EditText(this).apply { setText(device.name) }
        AlertDialog.Builder(this)
            .setTitle("Rename Device")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    repository.renameDevice(device.id, newName,
                        onSuccess = { Toast.makeText(this, "Device renamed.", Toast.LENGTH_SHORT).show() },
                        onFailure = { error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveDeviceDialog() {
        if (deviceList.isEmpty()) {
            Toast.makeText(this, "There are no devices to remove.", Toast.LENGTH_SHORT).show()
            return
        }
        val deviceNames = deviceList.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select a Device to Remove")
            .setItems(deviceNames) { _, which ->
                val selectedDevice = deviceList[which]
                if (selectedDevice.status.lowercase() != "online") {
                    showOfflineWarningDialog(selectedDevice)
                } else {
                    showRemoveConfirmationDialog(selectedDevice)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showOfflineWarningDialog(device: Device) {
        AlertDialog.Builder(this)
            .setTitle("Device is Offline")
            .setMessage("WARNING: '${device.name}' appears to be Offline.\n\n" +
                    "If you remove it now, it will NOT receive the reset command. You may be unable to re-pair it without reflashing the code.\n\n" +
                    "Please ensure the device is powered on and connected to WiFi first.")
            .setPositiveButton("Force Remove") { _, _ -> showRemoveConfirmationDialog(device) }
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showRemoveConfirmationDialog(device: Device) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Removal")
            .setMessage("Are you sure you want to remove '${device.name}'?")
            .setPositiveButton("Remove") { _, _ ->
                repository.removeDevice(device.id,
                    onSuccess = { Toast.makeText(this, "'${device.name}' removed.", Toast.LENGTH_SHORT).show() },
                    onFailure = { error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show() }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_settings
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish(); overridePendingTransition(0,0); true
                }
                R.id.navigation_activity -> {
                    startActivity(Intent(this, AllEventsActivity::class.java))
                    finish(); overridePendingTransition(0,0); true
                }
                R.id.navigation_settings -> true
                else -> false
            }
        }
    }

    private fun navigateToDeviceDetails(device: Device) {
        val intent = Intent(this, DeviceDetailsActivity::class.java)
        intent.putExtra("DEVICE_ID", device.id)
        startActivity(intent)
    }

    override fun navigateToDeviceDetails() {}
    override fun navigateToAddDevice() { startActivity(Intent(this, AddDeviceActivity::class.java)) }
    override fun navigateToRemoveDevice() { showRemoveDeviceDialog() }
    override fun navigateToProfile() { startActivity(Intent(this, ProfileActivity::class.java)) }
    override fun navigateToChangePassword() { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
    override fun showLogoutConfirmation() {
        AlertDialog.Builder(this).setTitle("Logout").setMessage("Are you sure?").setPositiveButton("Yes") { _, _ -> (presenter as SettingsPresenter).confirmLogout() }.setNegativeButton("Cancel", null).show()
    }
    override fun performLogout() {
        val intent = Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent); finish()
    }
    override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    override fun onDestroy() { super.onDestroy(); presenter.detachView() }
}