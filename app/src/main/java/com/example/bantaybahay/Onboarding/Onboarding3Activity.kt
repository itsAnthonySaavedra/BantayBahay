package com.example.bantaybahay.Onboarding
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.R

class Onboarding3Activity : Activity(), IOnboardingView {

    // MVP Components
    private lateinit var presenter: IOnboardingPresenter
    private lateinit var repository: OnboardingRepository

    // UI Elements
    private lateinit var getStartedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding3)

        // Initialize MVP components
        repository = OnboardingRepository(this)
        presenter = OnboardingPresenter(repository)
        presenter.attachView(this)

        // Initialize UI elements
        getStartedButton = findViewById(R.id.getStartedButton)

        // Set up click listener
        getStartedButton.setOnClickListener {
            presenter.onGetStartedClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- IOnboardingView Implementation ---

    override fun navigateToOnboarding2() {
        // Not used in screen 3
    }

    override fun navigateToOnboarding3() {
        // Not used in screen 3
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}