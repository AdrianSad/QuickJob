package com.example.quickjob.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.example.quickjob.Adapters.IntroViewPagerAdapter
import com.example.quickjob.Classes.ScreenItem
import com.example.quickjob.R

class IntroActivity : AppCompatActivity() {

    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var tabIndicator: TabLayout
    private lateinit var nextBtn: Button
    private var position: Int = 0
    private lateinit var startBtn: Button
    private lateinit var btnAnim : Animation
    private lateinit var mainActivity: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if(restorePrefData()){
            val homeActivity = Intent(applicationContext, HomeActivity::class.java)
            startActivity(homeActivity)
            finish()
        }
        setContentView(R.layout.activity_intro)

        initVariables()

        //Dodanie elementow do listy

        val mList: List<ScreenItem> = listOf(
            ScreenItem("Easy job posting","Just few clicks",R.drawable.img1),
            ScreenItem("Fast answer and help","Easy contact with another person", R.drawable.img2),
            ScreenItem("Various payment","You can pay in a variety of ways for example : money, favor, dinner etc.",R.drawable.img3)
        )

        introViewPagerAdapter = IntroViewPagerAdapter(this,mList)
        screenPager.adapter = introViewPagerAdapter
        tabIndicator.setupWithViewPager(screenPager)


        nextBtn.setOnClickListener{
            position = screenPager.currentItem

            if(position < mList.size){
                position++
                screenPager.setCurrentItem(position)
            }
            // Kiedy przejdziemy na ostatnia strone

            if(position == mList.size-1){
                loadLastScreen()
            }

        }

        startBtn.setOnClickListener{
            startActivity(mainActivity)
            savePrefsData()
            finish()

        }

        tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if(p0?.position == mList.size -1){

                    loadLastScreen()
                }
            }

        }

        )

    }

    private fun initVariables() {
        supportActionBar?.hide()

        mainActivity = Intent(applicationContext, HomeActivity::class.java)
        startBtn = findViewById(R.id.intro_start_btn)
        nextBtn = findViewById(R.id.intro_btn)
        tabIndicator = this.findViewById(R.id.tab_indicator)
        screenPager = findViewById(R.id.intro_viewPager)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
    }

    private fun restorePrefData(): Boolean {

        val pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isOpened:Boolean = pref.getBoolean("isIntroOpened",false)
        return isOpened

    }

    private fun savePrefsData() {

        val pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isIntroOpened",true)
        editor.commit()

    }

    private fun loadLastScreen() {

        nextBtn.visibility = View.INVISIBLE
        tabIndicator.visibility = View.INVISIBLE
        startBtn.visibility = View.VISIBLE

        startBtn.animation = btnAnim

    }
}
