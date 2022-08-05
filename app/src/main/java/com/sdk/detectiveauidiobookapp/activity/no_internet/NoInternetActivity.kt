package com.sdk.detectiveauidiobookapp.activity.no_internet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.card.MaterialCardView
import com.sdk.detectiveauidiobookapp.R
import com.sdk.detectiveauidiobookapp.activity.main.MainActivity
import com.sdk.detectiveauidiobookapp.util.NetworkHelper
import com.sdk.detectiveauidiobookapp.util.toast

class NoInternetActivity : AppCompatActivity() {
    private lateinit var networkHelper: NetworkHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        networkHelper = NetworkHelper(this)
        val btnCard: MaterialCardView = findViewById(R.id.btnCard)
        btnCard.setOnClickListener {
            if (networkHelper.isNetworkConnected()) {
                intent()
            } else {
                toast(getString(R.string.no_internet))
            }
        }
    }

    override fun onBackPressed() {
        if (networkHelper.isNetworkConnected()) {
            intent()
        } else
            finishAffinity()
    }
    private fun intent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}