package com.example.bantaybahay.Dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardPresenter(
    private val dashboardRepository: DashboardRepository
) : IDashboardPresenter {

    private var view: IDashboardView? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main)

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
        presenterScope.launch {
            dashboardRepository.sendArmCommand(
                onSuccess = {
                    // The real-time listener in getDeviceStatus will handle the UI update
                    view?.hideProgress()
                },
                onFailure = { errorMessage ->
                    view?.showError(errorMessage)
                    view?.hideProgress()
                }
            )
        }
    }

    override fun disarmSystem() {
        view?.showProgress()
        presenterScope.launch {
            dashboardRepository.sendDisarmCommand(
                onSuccess = {
                    // The real-time listener will handle the UI update
                    view?.hideProgress()
                },
                onFailure = { errorMessage ->
                    view?.showError(errorMessage)
                    view?.hideProgress()
                }
            )
        }
    }
}