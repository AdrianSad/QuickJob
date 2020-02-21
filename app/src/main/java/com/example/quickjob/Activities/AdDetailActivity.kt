package com.example.quickjob.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quickjob.Activities.EditActivities.*
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class AdDetailActivity : AppCompatActivity() {

    private lateinit var userImg : CircleImageView
    private lateinit var desc : TextView
    private lateinit var location : TextView
    private lateinit var payment : TextView
    private lateinit var msgBtn : Button
    private lateinit var phoneBtn : Button
    private lateinit var category : TextView
    private lateinit var userName : TextView
    private lateinit var date : TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var userID: String
    private lateinit var postID: String
    private lateinit var userPostID : String
    private lateinit var activityContext: Context
    private lateinit var toolbar : Toolbar
    private lateinit var toolbarImg : ImageView
    private lateinit var options : RequestOptions
    private lateinit var imgName : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_ad_detail)

        initVariables()
        setFields()

        //TODO: Send message and phone buttons' action
    }

    private fun initVariables() {
        toolbarImg = findViewById(R.id.ad_detail_img)
        toolbar = findViewById(R.id.ad_detail_toolbar)
        userImg = findViewById(R.id.ad_detail_user_img)
        desc = findViewById(R.id.ad_detail_desc)
        location = findViewById(R.id.ad_detail_location)
        payment = findViewById(R.id.ad_detail_payment)
        msgBtn = findViewById(R.id.ad_detail_msg_btn)
        phoneBtn = findViewById(R.id.ad_detail_phone_btn)
        date = findViewById(R.id.ad_detail_date)
        category = findViewById(R.id.ad_detail_category)
        userName = findViewById(R.id.ad_detail_user_name)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.loading_shape)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        activityContext = this
        postID = intent.extras.getString(Constants.POST_ID)
        userPostID = intent.extras.getString(Constants.USER_POST_ID)
        userID = intent.extras.getString(Constants.USER_ID)
        imgName = intent.extras.getString(Constants.IMAGE_NAME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if (data != null) {
                    toolbar.title = data.getStringExtra(Constants.TITLE)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()


        if(intent.extras.getString(Constants.DESCRIPTION).isNotEmpty())
        desc.text = intent.extras.getString(Constants.DESCRIPTION)
        if(intent.extras.getString(Constants.CATEGORY).isNotEmpty())
        category.text = intent.extras.getString(Constants.CATEGORY)
        if(intent.extras.getString(Constants.LOCATION).isNotEmpty())
        location.text = intent.extras.getString(Constants.LOCATION)
        if(intent.extras.getString(Constants.PAYMENT).isNotEmpty()) {
            val newPayment = intent.extras.getString(Constants.PAYMENT)
            payment.text = newPayment
            changePaymentImg(newPayment)
        }
        if(intent.extras.getString(Constants.IMAGE).isNotEmpty()) {
            val adImg: String = intent.extras.getString(Constants.IMAGE)
            Glide.with(this).load(adImg).apply(options).into(toolbarImg)
        }


    }

    private fun setFields(){

        val adImg: String = intent.extras.getString(Constants.IMAGE)
        Glide.with(this).load(adImg).apply(options).into(toolbarImg)
        val adTitle : String = intent.extras.getString(Constants.TITLE)
        toolbar.title = adTitle
        val adDesc: String = intent.extras.getString(Constants.DESCRIPTION)
        desc.text = adDesc
        val adCat : String = intent.extras.getString(Constants.CATEGORY)
        category.text = adCat

        intent.extras.getString(Constants.PAYMENT).let {
            payment.text = it
            changePaymentImg(it)
        }

        val adLocation : String = intent.extras.getString(Constants.LOCATION)
        location.text = adLocation
        val adDate:String = intent.extras.getString(Constants.TIMESTAMP)
        date.text = adDate
        val userID : String = intent.extras.getString(Constants.USER_ID)
        firebaseFirestore.collection(Constants.USERS_PATH).document(userID).get().addOnSuccessListener {

            if(it != null) {

                if(it.data?.getValue(Constants.IMAGE) != null){
                    val imgUri = it.data!!.getValue(Constants.IMAGE)
                    Glide.with(this).load(imgUri).into(userImg)
                }

                if(it.data?.getValue(Constants.USER_NAME) != null){

                    val userNameText: String = it.data!!.getValue(Constants.USER_NAME) as String
                    userName.text = userNameText

                }
            }
        }
    }

    private fun changePaymentImg(it : String){
        if(it.toIntOrNull() == null) {
            payment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cake_black_24dp, 0, 0, 0)
            payment.setTextColor(Color.parseColor(Constants.PAYMENT_RED_COLOR))
        }
        else{
            payment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_money_black_24dp,0,0,0)
            payment.setTextColor(Color.parseColor(Constants.PAYMENT_GREEN_COLOR))
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupMenu() : Boolean {
        firebaseAuth.currentUser?.let {

            return userID == it.uid

        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        return if(setupMenu()) {
            inflater.inflate(R.menu.ad_menu, menu)
            true
        }else super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.ad_menu_edit -> {

                val editDialog = EditAdDialogFragment(activityContext, userID, postID, userPostID)
                editDialog.show(supportFragmentManager,"edit")
                true
            }
            R.id.ad_menu_delete -> {

                val deleteDialog = DeleteAdDialogFragment(firebaseFirestore,userID, postID, userPostID, activityContext, firebaseStorage, imgName)
                deleteDialog.show(supportFragmentManager,"delete")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public class DeleteAdDialogFragment(private val firestore: FirebaseFirestore, private val userID : String, private val postID : String, private val userPostID : String, private val activityContext: Context, private val storage: FirebaseStorage, private val imgName : String) : DialogFragment(){

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.delete_hint.toString())
                    .setPositiveButton(R.string.yes_btn.toString()) { dialog, id ->
                        firestore.collection(Constants.POSTS_PATH).document(postID).delete().addOnSuccessListener {

                                firestore.collection(Constants.USERS_PATH).document(userID).collection(Constants.POSTS_PATH).document(userPostID).delete().addOnCompleteListener {task ->

                                    if(task.isSuccessful){

                                        val deleteReference = storage.reference.child(Constants.IMAGES_PATH).child(imgName)
                                        deleteReference.delete().addOnCompleteListener { deleteTask ->

                                            if(deleteTask.isSuccessful){
                                                dismiss()
                                                (activityContext as Activity).finish()
                                            }
                                        }

                                    }

                            }
                        }
                    }
                    .setNegativeButton(R.string.no_btn.toString(),DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")

        }
    }

    class EditAdDialogFragment(private val activityContext: Context, private val userID: String, private val postID: String, private val userPostID: String) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.edit_ad_title)
                    .setItems(R.array.edit_ad_array) { dialog, which ->
                        val editIntent = Intent(activityContext, EditTitleActivity::class.java)
                        editIntent.putExtra(Constants.USER_ID, userID)
                        editIntent.putExtra(Constants.POST_ID, postID)
                        editIntent.putExtra(Constants.USER_POST_ID,userPostID)

                        when(which){
                            0 -> {
                                val title : String = activity!!.intent.extras.getString(Constants.TITLE)
                                editIntent.putExtra(Constants.TITLE, title)
                                startActivityForResult(editIntent,1)
                            }

                            1 -> {
                                val desc: String = activity!!.intent.extras.getString(Constants.DESCRIPTION)
                                editIntent.putExtra(Constants.DESCRIPTION, desc)
                                editIntent.setClass(activityContext, EditDescActivity::class.java)
                                activityContext.startActivity(editIntent)
                            }

                            2 -> {
                                val imgName: String = activity!!.intent.extras.getString(Constants.IMAGE_NAME)
                                val img: String = activity!!.intent.extras.getString(Constants.IMAGE)
                                editIntent.putExtra(Constants.IMAGE_NAME, imgName)
                                editIntent.putExtra(Constants.IMAGE, img)
                                editIntent.setClass(activityContext, EditImageActivity::class.java)
                                activityContext.startActivity(editIntent)
                            }

                            3 -> {
                                val cat: String = activity!!.intent.extras.getString(Constants.CATEGORY)
                                editIntent.putExtra(Constants.CATEGORY, cat)
                                editIntent.setClass(activityContext, EditCategoryActivity::class.java)
                                activityContext.startActivity(editIntent)
                            }

                            4 -> {
                                val location: String = activity!!.intent.extras.getString(Constants.LOCATION)
                                editIntent.putExtra(Constants.LOCATION, location)
                                editIntent.setClass(activityContext, EditLocationActivity::class.java)
                                activityContext.startActivity(editIntent)
                            }

                            5 -> {
                                val payment: String = activity!!.intent.extras.getString(Constants.PAYMENT)
                                editIntent.putExtra(Constants.PAYMENT, payment)
                                editIntent.setClass(activityContext, EditPaymentActivity::class.java)
                                activityContext.startActivity(editIntent)
                            }
                        }
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }


}
