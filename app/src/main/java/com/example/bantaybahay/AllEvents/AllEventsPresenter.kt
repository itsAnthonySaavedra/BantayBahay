package com.example.bantaybahay.AllEvents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllEventsPresenter(private val repository: AllEventsRepository) {
    private var view: IAllEventsView? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main)

    fun attachView(view: IAllEventsView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun loadAllEvents() {
        view?.showLoading()
        presenterScope.launch {
            repository.getAllUserEvents { events ->
                view?.hideLoading()
                if (events.isNotEmpty()) {
                    view?.displayEvents(events)
                } else {
                    view?.showEmptyState()
                }
            }
        }
    }
}