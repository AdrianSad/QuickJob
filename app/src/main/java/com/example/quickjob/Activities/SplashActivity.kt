package com.example.quickjob.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val r = Runnable { val intent = Intent(applicationContext,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        Handler().postDelayed(r , Constants.SPLASH_SCREEN_TIME.toLong())
    }
}
