package com.example.bantaybahay.Dashboard
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.AllEvents.AllEventsActivity
import com.example.bantaybahay.Dashboard.Adapters.RecentActivityAdapter
import com.example.bantaybahay.R
import com.example.bantaybahay.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : Activity(), IDashboardView {

    private lateinit var presenter: IDashboardPresenter
    private lateinit var dashboardRepository: DashboardRepository
    private lateinit var statusTextView: TextView
    private lateinit var actionButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recentEventsList: RecyclerView
    private lateinit var noEventsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dashboardRepository = DashboardRepository()
        presenter = DashboardPresenter(dashboardRepository)
        presenter.attachView(this)

        statusTextView = findViewById(R.id.statusTitle)
        actionButton = findViewById(R.id.actionButton)
        progressBar = findViewById(R.id.progressBar)
        recentEventsList = findViewById(R.id.activityList)
        noEventsTextView = findViewById(R.id.noEventsText)

        actionButton.setOnClickListener {
            if (statusTextView.text.toString() == "System Armed") {
                presenter.disarmSystem()
            } else {
                presenter.armSystem()
            }
        }

        presenter.loadDashboardData()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true // Already here
                R.id.navigation_activity -> {
                    startActivity(Intent(this, AllEventsActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0) // No animation
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0) // No animation
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() { super.onDestroy(); presenter.detachView() }
    override fun showProgress() { progressBar.visibility = View.VISIBLE }
    override fun hideProgress() { progressBar.visibility = View.GONE }
    override fun setSystemStatus(status: String) {
        statusTextView.text = status
        actionButton.text = if (status == "System Armed") "Disarm" else "Arm"
    }
    override fun showRecentEvents(events: List<Event>) {
        if (events.isEmpty()) {
            noEventsTextView.visibility = View.VISIBLE
            recentEventsList.visibility = View.GONE
        } else {
            noEventsTextView.visibility = View.GONE
            recentEventsList.visibility = View.VISIBLE
            recentEventsList.layoutManager = LinearLayoutManager(this)
            recentEventsList.adapter = RecentActivityAdapter(events)
        }
    }
    override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
}