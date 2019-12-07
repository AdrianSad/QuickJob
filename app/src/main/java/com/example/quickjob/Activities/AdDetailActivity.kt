package com.example.quickjob.Activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.quickjob.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AdDetailActivity : AppCompatActivity() {

    lateinit var userImg : CircleImageView
    lateinit var desc : TextView
    lateinit var location : TextView
    lateinit var payment : TextView
    lateinit var msgBtn : Button
    lateinit var phoneBtn : Button
    lateinit var category : TextView
    lateinit var userName : TextView
    lateinit var date : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_ad_detail)

        val toolbarImg : ImageView = findViewById(R.id.ad_detail_img)
        val toolbar : Toolbar = findViewById(R.id.ad_detail_toolbar)
        userImg = findViewById(R.id.ad_detail_user_img)
        desc = findViewById(R.id.ad_detail_desc)
        location = findViewById(R.id.ad_detail_location)
        payment = findViewById(R.id.ad_detail_payment)
        msgBtn = findViewById(R.id.ad_detail_msg_btn)
        phoneBtn = findViewById(R.id.ad_detail_phone_btn)
        date = findViewById(R.id.ad_detail_date)
        category = findViewById(R.id.ad_detail_category)
        userName = findViewById(R.id.ad_detail_user_name)
        val firebaseFirestore = FirebaseFirestore.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val adImg: String = intent.extras.getString("img")
        Glide.with(this).load(adImg).into(toolbarImg)

        val adTitle : String = intent.extras.getString("title")
        toolbar.title = adTitle

        val adDesc: String = intent.extras.getString("desc")
        desc.text = adDesc

        val adCat : String = intent.extras.getString("category")
        category.text = adCat

        val adPayment : String = intent.extras.getString("payment")
        payment.text = adPayment

        val adLocation : String = intent.extras.getString("location")
        location.text = adLocation


        val adDate:String = intent.extras.getString("timestamp")
        date.text = adDate


        val userID : String = intent.extras.getString("user")
        firebaseFirestore.collection("users").document(userID).get().addOnSuccessListener {

            if(it != null) {

                if(it.data?.getValue("img") != null){
                    val imgUri = it.data!!.getValue("img")
                    Glide.with(this).load(imgUri).into(userImg)
                }

                if(it.data?.getValue("name") != null){

                    val userNameText: String = it.data!!.getValue("name") as String
                    userName.text = userNameText

                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
