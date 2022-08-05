package com.sdk.detectiveauidiobookapp.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.sdk.detectiveauidiobookapp.R
import com.sdk.detectiveauidiobookapp.activity.main.MainActivity
import com.sdk.detectiveauidiobookapp.manager.CheckInternetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var checkInternetManager: CheckInternetManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkInternetManager = CheckInternetManager()

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            initViews()
        }
    }
    private fun initViews() {
        val intent = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(checkInternetManager, intent)
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (checkInternetManager.hasInternet(this@SplashActivity)) {
                    val start = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(start)
                    finish()
                }
            }
        }.start()
    }
}