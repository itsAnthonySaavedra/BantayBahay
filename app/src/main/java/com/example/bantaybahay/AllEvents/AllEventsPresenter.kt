package com.example.bantaybahay.AllEvents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllEventsPresenter(
    private val view: AllEventsView,
    private val repository: AllEventsRepository = AllEventsRepository()
) : AllEventsRepository.LogsListener {

    fun loadEvents() {
        view.showLoading()
        repository.getAllLogs(this)
    }

    override fun onLogsLoaded(logs: Map<String, String>) {
        view.hideLoading()

        if (logs.isEmpty()) {
            view.showNoEvents()
            return
        }

        val sorted = logs.entries
            .sortedByDescending { it.key }        // newest → oldest
            .map { it.toPair() }                  // convert to List<Pair>

        view.showAllEvents(sorted)
    }

    override fun onError(message: String) {
        view.hideLoading()
        view.showError(message)
    }
}