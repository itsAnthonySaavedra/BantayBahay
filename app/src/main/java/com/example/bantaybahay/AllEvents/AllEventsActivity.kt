package com.example.bantaybahay.AllEvents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.R
import com.example.bantaybahay.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllEventsActivity : Activity(), IAllEventsView {

    private lateinit var presenter: AllEventsPresenter
    private lateinit var repository: AllEventsRepository
    private lateinit var allEventsRecyclerView: RecyclerView
    private lateinit var tvNoEvents: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AllEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_events)

        initializeViews()
        setupRecyclerView()
        setupBottomNavigation()

        repository = AllEventsRepository()
        presenter = AllEventsPresenter(repository)
        presenter.attachView(this)

        presenter.loadAllEvents()
    }

    private fun initializeViews() {
        allEventsRecyclerView = findViewById(R.id.allEventsRecyclerView)
        tvNoEvents = findViewById(R.id.tvNoEvents)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        adapter = AllEventsAdapter()
        allEventsRecyclerView.layoutManager = LinearLayoutManager(this)
        allEventsRecyclerView.adapter = adapter
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_activity

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_activity -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() { super.onDestroy(); presenter.detachView() }
    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun displayEvents(events: List<TitledEvent>) {
        allEventsRecyclerView.visibility = View.VISIBLE
        tvNoEvents.visibility = View.GONE
        adapter.updateEvents(events)
    }
    override fun showEmptyState() {
        allEventsRecyclerView.visibility = View.GONE
        tvNoEvents.visibility = View.VISIBLE
    }
    override fun showError(message: String) { Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show() }
}