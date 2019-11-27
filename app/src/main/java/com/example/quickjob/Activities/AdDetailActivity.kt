package com.example.quickjob.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class AdDetailActivity : AppCompatActivity() {

    lateinit var postImg : ImageView
    lateinit var userImg : CircleImageView
    lateinit var title : TextView
    lateinit var desc : TextView
    lateinit var location : TextView
    lateinit var payment : TextView
    lateinit var msgBtn : Button
    lateinit var phoneBtn : Button
    lateinit var category : TextView
    lateinit var userName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        postImg = findViewById(R.id.ad_detail_img)
        userImg = findViewById(R.id.ad_detail_user_img)
        title = findViewById(R.id.ad_detail_title)
        desc = findViewById(R.id.ad_detail_desc)
        location = findViewById(R.id.ad_detail_location)
        payment = findViewById(R.id.ad_detail_payment)
        msgBtn = findViewById(R.id.ad_detail_msg_btn)
        phoneBtn = findViewById(R.id.ad_detail_phone_btn)
        category = findViewById(R.id.ad_detail_category)
        userName = findViewById(R.id.ad_detail_user_name)
        val firebaseFirestore = FirebaseFirestore.getInstance()

        val adImg: String = intent.extras.getString("img")
        Glide.with(this).load(adImg).into(postImg)

        val adTitle : String = intent.extras.getString("title")
        title.text = adTitle

        val adDesc: String = intent.extras.getString("desc")
        desc.text = adDesc

        val adCat : String = intent.extras.getString("category")
        category.text = adCat

        val adPayment : String = intent.extras.getString("payment")
        payment.text = adPayment

        val adLocation : String = intent.extras.getString("location")
        location.text = adLocation

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
}
