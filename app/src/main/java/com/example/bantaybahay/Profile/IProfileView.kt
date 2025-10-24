package com.example.bantaybahay.Profile

interface IProfileView {
    fun showProgress()
    fun hideProgress()
    fun displayUserProfile(name: String, email: String, phone: String?, location: String?, photoUrl: String?)
    fun showUpdateSuccess(message: String)
    fun showError(message: String)
    fun navigateBack()
}