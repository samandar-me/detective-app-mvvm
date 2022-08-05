package com.sdk.detectiveauidiobookapp.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sdk.detectiveauidiobookapp.R
import com.sdk.detectiveauidiobookapp.activity.no_internet.NoInternetActivity
import com.sdk.detectiveauidiobookapp.util.NetworkHelper
import com.sdk.detectiveauidiobookapp.util.toast

class CheckInternetManager : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!hasInternet(context)) {
            context?.toast(context.getString(R.string.no_internet))
            context?.startActivity(Intent(context, NoInternetActivity::class.java))
        }
    }

    fun hasInternet(context: Context?): Boolean {
        val networkHelper = NetworkHelper(context!!)
        return networkHelper.isNetworkConnected()
    }
}