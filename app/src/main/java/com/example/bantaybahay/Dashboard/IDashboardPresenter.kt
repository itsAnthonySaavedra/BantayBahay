package com.example.bantaybahay.Dashboard

interface IDashboardPresenter {
    fun attachView(view: IDashboardView)
    fun detachView()
    fun loadDashboardData()
    fun armSystem()
    fun disarmSystem()
}