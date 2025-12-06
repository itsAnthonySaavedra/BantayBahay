package com.example.bantaybahay.DeviceDetails

interface IDeviceDetailsView {
    fun showLoading()
    fun hideLoading()
    fun displayDeviceDetails(name: String, id: String, status: String, isArmed: Boolean)
    fun showSaveSuccess(message: String)
    fun showOfflineWarning(isOffline: Boolean) // New Method
    fun showError(message: String)
    fun closeScreen()
}