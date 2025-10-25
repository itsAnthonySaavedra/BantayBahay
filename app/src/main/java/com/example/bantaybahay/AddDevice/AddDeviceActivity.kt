package com.example.bantaybahay.AddDevice

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText

class AddDeviceActivity : Activity(), IAddDeviceView {

    private lateinit var presenter: AddDevicePresenter
    private lateinit var repository: AddDeviceRepository

    private lateinit var btnConnect: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var backArrow: ImageView
    private lateinit var etSsid: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure you are using the original layout with SSID/Password fields
        setContentView(R.layout.activity_add_device)

        repository = AddDeviceRepository()
        presenter = AddDevicePresenter(repository)
        presenter.attachView(this)

        // Initialize UI
        btnConnect = findViewById(R.id.btnConnectDevice)
        loadingDialog = LoadingDialog(this)
        backArrow = findViewById(R.id.backArrow) // Assuming you added a back arrow
        etSsid = findViewById(R.id.etWifiSsid)
        etPassword = findViewById(R.id.etWifiPassword)

        // --- SIMULATION LOGIC ---
        btnConnect.setOnClickListener {
            // In a real scenario, you would connect to the hotspot and send SSID/password.
            // For our simulation, we IGNORE the text fields and immediately search Firebase.
            Toast.makeText(this, "SIMULATION: Ignoring WiFi fields, searching Firebase directly.", Toast.LENGTH_LONG).show()
            presenter.startDeviceSearch()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showLoading(message: String) {
        loadingDialog.show(message)
    }

    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun onDeviceClaimed(deviceId: String) {
        Toast.makeText(this, "Device '$deviceId' claimed successfully!", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun showClaimingError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}