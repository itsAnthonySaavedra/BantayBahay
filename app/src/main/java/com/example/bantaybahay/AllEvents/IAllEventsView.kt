package com.example.bantaybahay.AllEvents

interface AllEventsView {
    fun showAllEvents(logs: List<Triple<String, String, String>>)
    fun showLoading()
    fun hideLoading()
    fun showNoEvents()
    fun showError(message: String)
}
