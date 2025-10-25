package com.example.bantaybahay.AllEvents

interface IAllEventsView {
    fun showLoading()
    fun hideLoading()
    fun displayEvents(events: List<TitledEvent>)
    fun showEmptyState()
    fun showError(message: String)
}