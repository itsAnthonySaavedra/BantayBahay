package com.example.bantaybahay.Profile

import android.net.Uri

interface IProfilePresenter {
    fun attachView(view: IProfileView)
    fun detachView()
    fun loadUserProfile()
    fun onPhotoSelected(uri: Uri)
    fun onSaveClicked(name: String, phone: String, location: String)
    fun onLocationIconClicked()
}