package com.example.bantaybahay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bantaybahay.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

        finish()
    }
}