package com.example.quickjob.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.quickjob.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            val mapIntent = Intent(applicationContext,MapsActivity::class.java)
            startActivity(mapIntent)
        }

        button2.setOnClickListener{
            val introIntent = Intent(applicationContext,IntroActivity::class.java)
            startActivity(introIntent)
            finish()
        }
    }

}
