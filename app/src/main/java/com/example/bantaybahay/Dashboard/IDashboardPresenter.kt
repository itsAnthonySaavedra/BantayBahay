package com.example.bantaybahay.Dashboard
import com.example.bantaybahay.Dashboard.DashboardView

interface IDashboardPresenter {
    fun attachView(view: DashboardView)
    fun detachView()
    fun loadDashboardData()
    fun armSystem()
    fun disarmSystem()
}
