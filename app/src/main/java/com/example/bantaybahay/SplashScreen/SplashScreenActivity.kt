package com.example.bantaybahay.SplashScreen
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.bantaybahay.Dashboard.DashboardActivity
import com.example.bantaybahay.Login.LoginActivity
import com.example.bantaybahay.R

class SplashScreenActivity : Activity(), ISplashView {

    private lateinit var presenter: ISplashPresenter
    private lateinit var repository: SplashRepository

    private val SPLASH_DELAY = 2500L // 2.5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Hide status bar
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

        // Initialize MVP
        repository = SplashRepository()
        presenter = SplashPresenter(repository)
        presenter.attachView(this)

        // Delay and check authentication
        Handler(Looper.getMainLooper()).postDelayed({
            presenter.checkUserAuthentication()
        }, SPLASH_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // --- ISplashView Implementation ---

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}