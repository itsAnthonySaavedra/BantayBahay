package com.example.bantaybahay.Dashboard

interface IDashboardView {
    fun showProgress()
    fun hideProgress()
    fun setSystemStatus(status: String)
    fun showRecentEvents(events: List<Event>)
    fun showError(message: String)
}
