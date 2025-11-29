package com.example.bantaybahay.AddDevice

interface IAddDeviceView {
    fun showLoading(message: String)
    fun hideLoading()
    fun onDeviceClaimed(deviceId: String)
    fun showClaimingError(message: String)
}
