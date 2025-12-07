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
    private lateinit var swArmStatus: com.google.android.material.switchmaterial.SwitchMaterial
    private lateinit var tvDeviceNameHeader: TextView
    private lateinit var btnSaveChanges: Button
    
    // New UI Elements
    private lateinit var swAutoArm: com.google.android.material.switchmaterial.SwitchMaterial
    private lateinit var tvAutoArmTime: TextView

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
        swArmStatus = findViewById(R.id.swArmStatus)
        tvDeviceNameHeader = findViewById(R.id.tvDeviceNameHeader)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        
        swAutoArm = findViewById(R.id.swAutoArm)
        tvAutoArmTime = findViewById(R.id.tvAutoArmTime)
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

        swArmStatus.setOnCheckedChangeListener { _, isChecked ->
            if (swArmStatus.isPressed) {
                presenter.onArmToggled(isChecked)
            }
        }
        
        swAutoArm.setOnCheckedChangeListener { _, isChecked ->
            if (swAutoArm.isPressed) {
                presenter.onAutoArmToggled(isChecked)
            }
        }
        
        tvAutoArmTime.setOnClickListener {
            presenter.onAutoArmTimeClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showLoading() { loadingDialog.show("Please wait...") }
    override fun hideLoading() { loadingDialog.dismiss() }

    override fun displayDeviceDetails(name: String, id: String, status: String, isArmed: Boolean, autoArmEnabled: Boolean, autoArmTime: String) {
        originalDeviceName = name
        tvDeviceNameHeader.text = name
        etDeviceName.setText(name)
        tvDeviceId.text = id
        tvDeviceStatus.text = status.replaceFirstChar { it.uppercase() }
        
        if (swArmStatus.isChecked != isArmed) {
            swArmStatus.isChecked = isArmed
        }
        
        // Auto-Arm Updates
        if (swAutoArm.isChecked != autoArmEnabled) {
            swAutoArm.isChecked = autoArmEnabled
        }
        tvAutoArmTime.text = "Time: $autoArmTime"
    }

    override fun showSaveSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        // Determine layout logic if needed, usually we hide buttons etc.
        if (message.contains("renamed")) {
             btnSaveChanges.visibility = View.GONE
        }
    }

    override fun showOfflineWarning(isOffline: Boolean) {
        val warningBanner = findViewById<TextView>(R.id.tvDeviceStatus) // Using status text for now
        
        if (isOffline) {
            warningBanner.text = "OFFLINE - Tap to Re-Pair"
            warningBanner.setTextColor(android.graphics.Color.RED)
            warningBanner.setOnClickListener {
                // Navigate to Pair Screen with ID pre-filled
                val intent = android.content.Intent(this, com.example.bantaybahay.AddDevice.AddDeviceActivity::class.java)
                startActivity(intent)
            }
        } else {
            warningBanner.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            warningBanner.setOnClickListener(null) // Remove click listener
        }
    }
    
    override fun showTimePicker(currentTime: String) {
        val calendar = java.util.Calendar.getInstance()
        var hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        var minute = calendar.get(java.util.Calendar.MINUTE)

        if (currentTime != "--:--" && currentTime.contains(":")) {
            val parts = currentTime.split(":")
            if (parts.size == 2) {
                hour = parts[0].toIntOrNull() ?: hour
                minute = parts[1].toIntOrNull() ?: minute
            }
        }

        android.app.TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            presenter.onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, false).show() // false for AM/PM mode
    }

    override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    override fun closeScreen() { finish() }
}