package com.example.bantaybahay.AddDevice

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.bantaybahay.R
import com.example.bantaybahay.Utils.LoadingDialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.google.android.material.textfield.TextInputEditText

class AddDeviceActivity : Activity(), IAddDeviceView {

    private lateinit var presenter: AddDevicePresenter
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var btnConnect: Button
    private lateinit var backArrow: ImageView
    private lateinit var etDeviceId: TextInputEditText
    private lateinit var etSsid: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        presenter = AddDevicePresenter(AddDeviceRepository())
        presenter.attachView(this)

        loadingDialog = LoadingDialog(this)

        // UI References
        btnConnect = findViewById(R.id.btnConnectDevice)
        backArrow = findViewById(R.id.backArrow)
        etDeviceId = findViewById(R.id.etDeviceId)
        etSsid = findViewById(R.id.etWifiSsid)
        etPassword = findViewById(R.id.etWifiPassword)

        // Connect button
        btnConnect.setOnClickListener {
            val deviceId = etDeviceId.text.toString().trim()
            val ssid = etSsid.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (deviceId.isEmpty() || ssid.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            presenter.pairDevice(deviceId, ssid, password)
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private fun bindToWifiNetwork(bind: Boolean) {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        if (!bind) {
            connectivityManager.bindProcessToNetwork(null)
            networkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
                networkCallback = null
            }
            return
        }

        // If already bound, don't do it again
        if (networkCallback != null) return

        // OPTIMIZATION: Check if we are already on WiFi
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            try {
                connectivityManager.bindProcessToNetwork(currentNetwork)
                runOnUiThread {
                    loadingDialog?.setMessage("WiFi Connected (Instant)! Sending...")
                }
                return // Done!
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback: Request it
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            // CRITICAL: Allow networks without internet (like ESP32 AP)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    connectivityManager.bindProcessToNetwork(network)
                    runOnUiThread {
                        loadingDialog?.setMessage("WiFi Connected! Sending...")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onLost(network: Network) {
                connectivityManager.bindProcessToNetwork(null)
            }
        }

        connectivityManager.requestNetwork(builder.build(), networkCallback!!)
    }

    override fun bindNetworkToWifi(bind: Boolean) {
        bindToWifiNetwork(bind)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        // Ensure we unregister callback to prevent leaks/crashes
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }

    // --- IAddDeviceView IMPLEMENTATION ---

    override fun showLoading(message: String) {
        loadingDialog.show(message)
    }

    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun onDeviceClaimed(deviceId: String) {
        Toast.makeText(this, "Device '$deviceId' paired successfully!", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun showClaimingError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}
