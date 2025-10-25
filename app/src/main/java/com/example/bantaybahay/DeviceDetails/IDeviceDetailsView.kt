package com.example.bantaybahay.DeviceDetails

interface IDeviceDetailsView {
    fun showLoading()
    fun hideLoading()
    fun displayDeviceDetails(name: String, id: String, status: String, armStatus: String)
    fun showSaveSuccess(message: String)
    fun showError(message: String)
    fun closeScreen()
}