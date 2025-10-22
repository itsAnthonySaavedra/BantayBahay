package com.example.bantaybahay.Settings

interface ISettingsView {
    fun navigateToDeviceDetails()
    fun navigateToAddDevice()
    fun navigateToRemoveDevice()
    fun navigateToProfile()
    fun navigateToChangePassword()
    fun showLogoutConfirmation()
    fun performLogout()
    fun showError(message: String)
}