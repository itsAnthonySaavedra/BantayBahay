package com.example.bantaybahay.DeviceDetails

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import com.google.android.material.textfield.TextInputEditText

class DeviceDetailsActivity : Activity(), IDeviceDetailsView {

    private lateinit var presenter: DeviceDetailsPresenter
    private lateinit var repository: DeviceDetailsRepository
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var backArrow: ImageView
    private lateinit var etDeviceName: TextInputEditText
    private lateinit var tvDeviceId: TextView
    private lateinit var tvDeviceStatus: TextView
    private lateinit var tvArmStatus: TextView
    private lateinit var tvDeviceNameHeader: TextView
    private lateinit var btnSaveChanges: Button

    private var originalDeviceName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)

        val deviceId = intent.getStringExtra("DEVICE_ID")
        if (deviceId == null) {
            Toast.makeText(this, "Error: Device ID missing.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initializeViews()
        loadingDialog = LoadingDialog(this)
        repository = DeviceDetailsRepository()
        presenter = DeviceDetailsPresenter(repository, deviceId)
        presenter.attachView(this)

        setupListeners()
    }

    private fun initializeViews() {
        backArrow = findViewById(R.id.backArrow)
        etDeviceName = findViewById(R.id.etDeviceName)
        tvDeviceId = findViewById(R.id.tvDeviceId)
        tvDeviceStatus = findViewById(R.id.tvDeviceStatus)
        tvArmStatus = findViewById(R.id.tvArmStatus)
        tvDeviceNameHeader = findViewById(R.id.tvDeviceNameHeader)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
    }

    private fun setupListeners() {
        backArrow.setOnClickListener { finish() }

        btnSaveChanges.setOnClickListener {
            val newName = etDeviceName.text.toString().trim()
            if (newName.isNotEmpty()) {
                presenter.onSaveClicked(newName)
            }
        }

        etDeviceName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btnSaveChanges.visibility = if (s.toString() != originalDeviceName) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showLoading() { loadingDialog.show("Please wait...") }
    override fun hideLoading() { loadingDialog.dismiss() }

    override fun displayDeviceDetails(name: String, id: String, status: String, armStatus: String) {
        originalDeviceName = name
        tvDeviceNameHeader.text = name
        etDeviceName.setText(name)
        tvDeviceId.text = id
        tvDeviceStatus.text = status.replaceFirstChar { it.uppercase() }
        tvArmStatus.text = armStatus
    }

    override fun showSaveSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        btnSaveChanges.visibility = View.GONE // Hide button after successful save
    }

    override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    override fun closeScreen() { finish() }
}