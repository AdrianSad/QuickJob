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

    lateinit var screenPager: ViewPager
    lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    lateinit var tabIndicator: TabLayout
    lateinit var nextBtn: Button
    var position: Int = 0
    lateinit var startBtn: Button
    lateinit var btnAnim : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_intro)

        // Full window i ukrycie action bar

        supportActionBar?.hide()

        val mainActivity: Intent = Intent(applicationContext,MainActivity::class.java)

        /*if(restorePrefData()){

            startActivity(mainActivity)
            finish()

        }*/

        startBtn = findViewById(R.id.intro_start_btn)
        nextBtn = findViewById(R.id.intro_btn)
        tabIndicator = this.findViewById(R.id.tab_indicator)
        screenPager = findViewById(R.id.intro_viewPager)
        btnAnim = AnimationUtils.loadAnimation(applicationContext,R.anim.button_animation)


        //Dodanie elementow do listy

        val mList: List<ScreenItem> = listOf(
            ScreenItem("Easy job posting","asdfasdas",R.drawable.img1),
            ScreenItem("Fast answer and help","benc benc", R.drawable.img2),
            ScreenItem("Various payment","",R.drawable.img3)
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
