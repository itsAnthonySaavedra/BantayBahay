package com.example.bantaybahay.Dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bantaybahay.R
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Adapters.SensorLogAdapter
import com.example.bantaybahay.AllEvents.AllEventsActivity
import com.example.bantaybahay.Notifications.NotificationPresenter
import com.example.bantaybahay.Notifications.NotificationView
import com.example.bantaybahay.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : AppCompatActivity(), DashboardView {


    private lateinit var presenter: DashboardPresenter
    private lateinit var statusTitle: TextView
    private lateinit var statusSubtitle: TextView
    private lateinit var statusCard: CardView
    private lateinit var statusIcon: ImageView
    private lateinit var activityList: RecyclerView
    private lateinit var adapter: SensorLogAdapter
    private lateinit var actionButton: AppCompatButton

    private var isArmed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        statusTitle = findViewById(R.id.statusTitle)
        statusSubtitle = findViewById(R.id.statusSubtitle)
        statusCard = findViewById(R.id.statusCard)
        statusIcon = findViewById(R.id.statusIcon)
        activityList = findViewById(R.id.activityList)

        actionButton = findViewById(R.id.actionButton)

        actionButton.setOnClickListener {
            val newArmedState = !isArmed    // toggle armed/disarmed
            presenter.setArmedState(newArmedState)

            if (newArmedState) {
                Toast.makeText(this, "System Armed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "System Disarmed", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = SensorLogAdapter()
        activityList.layoutManager = LinearLayoutManager(this)
        activityList.adapter = adapter

        presenter = DashboardPresenter(this)
        presenter.startListening()

        val notifPresenter = NotificationPresenter(object : NotificationView {
            override fun onTokenRegistered() {
                println("FCM Token saved to Firebase")
            }

            override fun onTokenRegistrationFailed() {
                println("Failed to register FCM token")
            }
        })
        notifPresenter.registerDeviceForNotifications()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true // already here

                R.id.navigation_activity -> {
                    startActivity(Intent(this, AllEventsActivity::class.java))
                    true
                }

                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    override fun updateSensorStatus(status: String) {
        runOnUiThread {
            if (status.equals("Open", ignoreCase = true)) {
                statusTitle.text = "Door Open"
                statusSubtitle.text = "Door is currently open"
                statusCard.setCardBackgroundColor(getColor(R.color.red_500))
            } else if (status.equals("Closed", ignoreCase = true)) {
                statusTitle.text = "Door Closed"
                statusSubtitle.text = "Your door is secure"
                statusCard.setCardBackgroundColor(getColor(R.color.green_500))
            } else {
                statusTitle.text = "Unknown"
                statusSubtitle.text = "Status unavailable"
                statusCard.setCardBackgroundColor(getColor(R.color.gray_500))
            }
        }
    }
    override fun updateArmedState(isArmedValue: Boolean) {
        isArmed = isArmedValue

        if (isArmed) {
            actionButton.text = "Disarm"
            actionButton.setBackgroundColor(getColor(R.color.red_500))   // GREEN WHEN ARMED
        } else {
            actionButton.text = "Arm"
            actionButton.setBackgroundColor(getColor(R.color.green_500))    // GRAY WHEN DISARMED
        }
    }
    // for disarm to be grey
    override fun showDisarmedState() {
        runOnUiThread {
            statusTitle.text = "System Disarmed"
            statusSubtitle.text = "Door Sensor is Offline"
            statusCard.setCardBackgroundColor(getColor(R.color.gray_500))
        }
    }

    override fun showSensorLogs(logs: Map<String, String>) {
        runOnUiThread {
            val sorted = logs.entries
                .sortedByDescending { it.key }
                .take(5)

            adapter.setLogs(sorted.map { it.toPair() })
        }
    }

    override fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, "Firebase error: $message", Toast.LENGTH_SHORT).show()
        }
    }

}
