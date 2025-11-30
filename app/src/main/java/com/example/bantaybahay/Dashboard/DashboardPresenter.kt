package com.example.bantaybahay.Dashboard

class DashboardPresenter(
    private val view: DashboardView,
    private val repository: DashboardRepository = DashboardRepository()
) : DashboardRepository.SensorListener {

    private var lastStatus: String = "Unknown"
    private var isArmed: Boolean = false

    fun startListening() {
        repository.listenToSensorData(this)
    }

    override fun onStatusChanged(status: String) {
        lastStatus = status
        updateUI()
    }

    override fun onLogsUpdated(logs: Map<String, String>) {

        // LIMIT TO 10 MOST RECENT LOGS
        val limitedLogs = logs
            .entries
            .sortedByDescending { it.key }   // newest first
            .take(10)                         // keep only 10
            .associate { it.toPair() }        // convert back to Map

        view.showSensorLogs(limitedLogs)
    }

    override fun onArmedChanged(isArmedValue: Boolean) {
        isArmed = isArmedValue
        view.updateArmedState(isArmed)
        updateUI()
    }

    override fun onError(message: String) {
        view.showError(message)
    }

    private fun updateUI() {
        if (!isArmed) {
            view.showDisarmedState()
        } else {
            view.updateSensorStatus(lastStatus)
        }
    }

    fun setArmedState(armed: Boolean) {
        repository.setArmed(armed)
    }

}
