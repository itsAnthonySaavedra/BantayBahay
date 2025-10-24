package com.example.bantaybahay.AddDevice

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog

class AddDeviceActivity : Activity(), IAddDeviceView {

    private lateinit var presenter: AddDevicePresenter
    private lateinit var repository: AddDeviceRepository

    private lateinit var btnConnect: Button
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        // Initialize MVP components
        repository = AddDeviceRepository()
        presenter = AddDevicePresenter(repository)
        presenter.attachView(this)

        // Initialize UI
        btnConnect = findViewById(R.id.btnConnectDevice)
        loadingDialog = LoadingDialog(this)

        btnConnect.setOnClickListener {
            presenter.startDeviceSearch()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- IAddDeviceView Implementation ---

    override fun showLoading(message: String) {
        loadingDialog.show(message)
    }

    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun onDeviceClaimed(deviceId: String) {
        Toast.makeText(this, "Device '$deviceId' added successfully!", Toast.LENGTH_LONG).show()
        finish() // Close the activity and return to settings
    }

    override fun showClaimingError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}