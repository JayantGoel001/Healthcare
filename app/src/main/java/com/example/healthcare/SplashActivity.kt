package com.example.healthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashScreen = object : Thread() {
            override fun run() {
                try {
                    sleep(5000)
                    val intent=Intent(this@SplashActivity, MainActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        splashScreen.start()
    }
}
