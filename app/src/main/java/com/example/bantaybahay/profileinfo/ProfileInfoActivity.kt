package com.example.bantaybahay.profileinfo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bantaybahay.databinding.ActivityProfileInfoBinding

class ProfileInfoActivity : AppCompatActivity(), ProfileInfoContract.View {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var presenter: ProfileInfoContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ProfileInfoPresenter(this)

        binding.signUpButton.setOnClickListener {
            val phone = binding.phoneEditTextOptional.text.toString().trim()
            val location = binding.locationEditTextOptional.text.toString().trim()
            presenter.onSignUpClicked(phone, location)
        }

        binding.skipText.setOnClickListener {
            presenter.onSkipClicked()
        }

        binding.profileImagePlaceholder.setOnClickListener {
            presenter.onAddPhotoClicked()
        }
    }

    override fun navigateToHomeScreen() {
        Toast.makeText(this, "Navigating to Home Screen!", Toast.LENGTH_SHORT).show()
        // For homescreen once compiled
    }

    override fun showLoading() {}
    override fun hideLoading() {}

    override fun showSaveError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}