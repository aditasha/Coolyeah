package com.papb.coolyeah.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.papb.coolyeah.Login
import com.papb.coolyeah.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}