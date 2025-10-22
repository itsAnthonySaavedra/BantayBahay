package com.example.bantaybahay.Detail

import android.net.Uri

interface IDetailsPresenter {
    fun attachView(view: IDetailsView)
    fun detachView()
    fun onPhotoSelected(uri: Uri)
    fun onNextClicked(phoneNumber: String, location: String)
    fun onSkipClicked()
}
