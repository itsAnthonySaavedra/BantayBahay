package com.example.bantaybahay.DeviceDetails

interface IDeviceDetailsView {
    fun showLoading()
    fun hideLoading()
    fun displayDeviceDetails(name: String, id: String, status: String, isArmed: Boolean, autoArmEnabled: Boolean, autoArmTime: String)
    fun showSaveSuccess(message: String)
    fun showOfflineWarning(isOffline: Boolean)
    fun showTimePicker(currentTime: String)
    fun showError(message: String)
    fun closeScreen()
}