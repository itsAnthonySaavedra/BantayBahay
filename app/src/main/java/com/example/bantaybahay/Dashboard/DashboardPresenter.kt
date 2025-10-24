package com.example.bantaybahay.Dashboard


class DashboardPresenter(
    private val dashboardRepository: DashboardRepository
) : IDashboardPresenter {

    private var view: IDashboardView? = null

    override fun attachView(view: IDashboardView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadDashboardData() {
        view?.showProgress()
        dashboardRepository.getDeviceStatus(
            onSuccess = { status ->
                view?.setSystemStatus(status)
                // Once status is loaded, load events
                dashboardRepository.getRecentEvents(
                    onSuccess = { events ->
                        view?.showRecentEvents(events)
                        view?.hideProgress()
                    },
                    onFailure = { errorMessage ->
                        view?.showError(errorMessage)
                        view?.hideProgress()
                    }
                )
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
                view?.hideProgress()
            }
        )
    }

    override fun armSystem() {
        view?.showProgress()
        dashboardRepository.sendArmCommand(
            onSuccess = {
                view?.setSystemStatus("System Armed")
                view?.hideProgress()
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
                view?.hideProgress()
            }
        )
    }

    override fun disarmSystem() {
        view?.showProgress()
        dashboardRepository.sendDisarmCommand(
            onSuccess = {
                view?.setSystemStatus("System Disarmed")
                view?.hideProgress()
            },
            onFailure = { errorMessage ->
                view?.showError(errorMessage)
                view?.hideProgress()
            }
        )
    }
}