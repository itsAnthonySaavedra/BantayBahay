package com.example.bantaybahay.Dashboard
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Dashboard.Adapters.RecentActivityAdapter
import com.example.bantaybahay.Dashboard.Events.DashboardEvent
import com.example.bantaybahay.R
import com.example.bantaybahay.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.LinearLayoutManager

class DashboardActivity : Activity(), IDashboardView {

    // MVP Components
    private lateinit var presenter: IDashboardPresenter
    private lateinit var dashboardRepository: DashboardRepository

    // UI Elements
    private lateinit var statusTextView: TextView
    private lateinit var actionButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recentEventsList: RecyclerView
    private lateinit var noEventsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize MVP components
        dashboardRepository = DashboardRepository()
        presenter = DashboardPresenter(dashboardRepository)
        presenter.attachView(this)

        // Initialize UI elements
        statusTextView = findViewById(R.id.statusTitle)
        actionButton = findViewById(R.id.actionButton)

        // Corrected: Uncommented the lines below to initialize the views
        progressBar = findViewById(R.id.progressBar)
        recentEventsList = findViewById(R.id.activityList)
        noEventsTextView = findViewById(R.id.noEventsText)

        // Set up button click listeners
        actionButton.setOnClickListener {
            // Tell the presenter that the user wants to change the status
            val currentStatus = statusTextView.text.toString()
            if (currentStatus == "System Armed") {
                presenter.disarmSystem()
            } else {
                presenter.armSystem()
            }
        }

        // Tell the presenter to load the initial data for the screen
        presenter.loadDashboardData()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already in dashboard, possibly reload or do nothing
                    true
                }
                R.id.navigation_activity -> {
                    // If you have an Activity tab, handle here
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- IDashboardView Implementation ---

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun setSystemStatus(status: String) {
        statusTextView.text = status
        // You would also change the card color and text here based on status
        if (status == "System Armed") {
            actionButton.text = "Disarm"
            // You can also change the card background color here
        } else {
            actionButton.text = "Arm"
            // Change color for disarmed state
        }
    }

    val demoEvents = listOf(
        DashboardEvent("Door Closed", "10:45 AM", "closed"),
        DashboardEvent("Door Opened", "10:40 AM", "opened"),
        DashboardEvent("Motion Detected", "10:35 AM", "detected")
    )

    override fun showRecentEvents(events: List<Event>) {
        if (events.isEmpty()) {
            noEventsTextView.visibility = View.VISIBLE
            recentEventsList.visibility = View.GONE
        } else {
            noEventsTextView.visibility = View.GONE
            recentEventsList.visibility = View.VISIBLE

            recentEventsList.layoutManager = LinearLayoutManager(this)
            recentEventsList.adapter = RecentActivityAdapter(demoEvents)

        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}