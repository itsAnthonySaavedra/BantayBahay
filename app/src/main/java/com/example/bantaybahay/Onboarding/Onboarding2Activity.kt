package com.example.bantaybahay.Onboarding
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.R

class Onboarding2Activity : Activity(), IOnboardingView {

    // MVP Components
    private lateinit var presenter: IOnboardingPresenter
    private lateinit var repository: OnboardingRepository

    // UI Elements
    private lateinit var nextButton: Button
    private lateinit var skipButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding2)

        // Initialize MVP components
        repository = OnboardingRepository(this)
        presenter = OnboardingPresenter(repository)
        presenter.attachView(this)

        // Initialize UI elements
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)

        // Set up click listeners
        nextButton.setOnClickListener {
            presenter.onNextClicked(2)  // Screen 2
        }

        skipButton.setOnClickListener {
            presenter.onSkipClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- IOnboardingView Implementation ---

    override fun navigateToOnboarding2() {
        // Not used in screen 2
    }

    override fun navigateToOnboarding3() {
        val intent = Intent(this, Onboarding3Activity::class.java)
        startActivity(intent)
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