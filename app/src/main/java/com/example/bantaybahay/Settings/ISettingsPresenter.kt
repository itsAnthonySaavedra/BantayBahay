package com.example.bantaybahay.Settings

interface ISettingsPresenter {
    fun attachView(view: ISettingsView)
    fun detachView()
    fun onDeviceClicked()
    fun onAddDeviceClicked()
    fun onRemoveDeviceClicked()
    fun onProfileClicked()
    fun onChangePasswordClicked()
    fun onLogoutClicked()
}