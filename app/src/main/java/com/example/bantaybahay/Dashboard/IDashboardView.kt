package com.example.bantaybahay.Dashboard

interface DashboardView {
    fun updateSensorStatus(status: String)
    fun showSensorLogs(logs: Map<String, String>)
    fun showDisarmedState()
    fun updateArmedState(isArmed: Boolean)
    fun showError(message: String)
}
