package com.example.bantaybahay.AllEvents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllEventsActivity : AppCompatActivity(), AllEventsView {

    private lateinit var presenter: AllEventsPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoEvents: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AllEventsAdapter
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tvClearEvents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_events)

        tvClearEvents = findViewById(R.id.tvClearEvents)
        tvClearEvents.setOnClickListener {
             // Optional: Add confirmation dialog
             presenter.clearEvents()
        }

        recyclerView = findViewById(R.id.allEventsRecyclerView)
        tvNoEvents = findViewById(R.id.tvNoEvents)
        progressBar = findViewById(R.id.progressBar)
        bottomNav = findViewById(R.id.bottomNavigationView)

        adapter = AllEventsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        presenter = AllEventsPresenter(this)
        presenter.loadEvents()

        bottomNav.selectedItemId = R.id.navigation_activity

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }

                R.id.navigation_activity -> true // already here

                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvNoEvents.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showAllEvents(logs: List<Triple<String, String, String>>) {
        recyclerView.visibility = View.VISIBLE
        tvNoEvents.visibility = View.GONE
        adapter.setEvents(logs)
    }

    override fun showNoEvents() {
        recyclerView.visibility = View.GONE
        tvNoEvents.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        tvNoEvents.text = message
        tvNoEvents.visibility = View.VISIBLE
    }
}
