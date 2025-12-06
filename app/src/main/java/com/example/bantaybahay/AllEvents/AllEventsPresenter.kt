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

    fun clearEvents() {
        repository.clearAllLogs()
        // The ValueEventListener in repository will trigger onLogsLoaded with empty list automatically?
        // Yes, if "logs" is deleted, onDataChange fires. But we might want to manually clear the view to be snappy.
        // Actually, since we use addValueEventListener in repository, removing the node triggers a callback.
        // But wait, if we delete "logs", the child loop might just yield nothing. 
        // Repository implementation: "if (!snapshot.exists()) ... onLogsLoaded(emptyList())". So it should work.
    }

    override fun onLogsLoaded(logs: List<Triple<String, String, String>>) {
        view.hideLoading()

        if (logs.isEmpty()) {
            view.showNoEvents()
            return
        }

        view.showAllEvents(logs)
    }

    override fun onError(message: String) {
        view.hideLoading()
        view.showError(message)
    }
}