package com.example.bantaybahay.SplashScreen

import com.google.firebase.auth.FirebaseAuth

class SplashRepository {

    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}