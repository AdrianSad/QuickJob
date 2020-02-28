package com.example.quickjob.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.schibstedspain.leku.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class NewAdActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var imgUri: Uri
    private val MAP_BUTTON_REQUEST_CODE = 3
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var locationText : TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_new_ad)

        showSnackBar(this,"Remember to fill all fields :)")

        img = findViewById(R.id.new_ad_img)
        val locationImg : ImageView = findViewById(R.id.new_ad_location_img)

        val titleField: TextInputLayout = findViewById(R.id.new_ad_title_field)
        val locationField: TextInputLayout = findViewById(R.id.new_ad_location_field)
        val paymentField: TextInputLayout = findViewById(R.id.new_ad_payment_field)
        val descField: TextInputLayout = findViewById(R.id.new_ad_desc_field)

        val titleText: TextInputEditText = findViewById(R.id.new_ad_title_text)
        locationText = findViewById(R.id.new_ad_location_text)
        val paymentText: TextInputEditText = findViewById(R.id.new_ad_payment_text)
        val descText: TextInputEditText = findViewById(R.id.new_ad_desc_text)

        val category: Spinner = findViewById(R.id.new_ad_spinner)
        val doneBtn: CircularProgressButton = findViewById(R.id.new_ad_btn_add)
        val toolbar: Toolbar = findViewById(R.id.new_ad_toolbar)

        val firebaseStorage = FirebaseStorage.getInstance()
        val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // IMAGE PICKER
        img.setOnClickListener{

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(410,200)
                .setAspectRatio(2,1)
                .start(this)
        }

        // SUBMIT BUTTON
        doneBtn.setOnClickListener{

            doneBtn.startAnimation()

            val title: String = titleText.text.toString()
            val location: String = locationText.text.toString()
            val desc: String = descText.text.toString()
            val payment: String = paymentText.text.toString()

            if(isFieldCorrect(title,titleField, 25) && isFieldCorrect(location,locationField) && isFieldCorrect(desc,descField, 200) && isFieldCorrect(payment, paymentField)){

                if(imgUri == null){

                    showSnackBar(this,"You have to add a image!")
                    doneBtn.revertAnimation()


                }else {

                    val randomName = UUID.randomUUID().toString()
                    val imgReference = firebaseStorage.reference.child(Constants.IMAGES_PATH).child("$randomName.jpg")
                    var uploadTask = imgReference.putFile(imgUri)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>  { task ->
                        if(!task.isSuccessful){
                            task.exception?.let {
                                doneBtn.revertAnimation()

                                throw it
                            }
                        }
                        return@Continuation imgReference.downloadUrl
                    }).addOnCompleteListener {

                        if(it.isSuccessful){
                            val downloadUrl = it.result.toString()

                            val adData = hashMapOf<String,Any>(
                                Constants.TITLE to title,
                                Constants.DESCRIPTION to desc,
                                "time" to FieldValue.serverTimestamp(),
                                Constants.IMAGE to downloadUrl,
                                Constants.LOCATION to location,
                                Constants.CATEGORY to category.selectedItem.toString(),
                                Constants.PAYMENT to payment,
                                Constants.USER_ID to currentUser!!.uid,
                                "img_name" to "$randomName.jpg"
                            )

                            if(longitude != 0.0 && latitude != 0.0){
                                val location: LatLng = LatLng(latitude,longitude)
                                adData[Constants.LATITUDE_LONGITUDE] = location
                            }

                            firebaseFirestore.collection(Constants.POSTS_PATH).add(adData).addOnSuccessListener { document ->

                                adData["id"] = document.id

                                    firebaseFirestore.collection(Constants.USERS_PATH).document(currentUser.uid).collection(Constants.POSTS_PATH).add(adData).addOnCompleteListener { task ->
                                        if(task.isSuccessful) {

                                            val postID: String = document.id
                                            val idHashMap = hashMapOf<String,Any>(
                                                Constants.USER_POST_ID to task.result?.id.toString(),
                                                Constants.POST_ID to postID
                                            )

                                            firebaseFirestore.collection(Constants.POSTS_PATH).document(postID).update(idHashMap).addOnCompleteListener {
                                                if(task.isSuccessful){
                                                    startDetailActivity(adData, idHashMap)
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                }.addOnFailureListener{ exception ->
                                doneBtn.revertAnimation()
                            }
                        }else {
                            doneBtn.revertAnimation()
                        }
                    }
                }
            }else {
                doneBtn.revertAnimation()
            }
        }

        // LOCATION PICKER
        locationImg.setOnClickListener {

            val locationPickerIntent = LocationPickerActivity.Builder()
                .withGeolocApiKey(R.string.google_maps_key.toString())
                //.withSearchZone("es_ES")
                //.withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
                .withDefaultLocaleSearchZone()
                //.shouldReturnOkOnBackPressed()
                //.withStreetHidden()
                //.withCityHidden()
                //.withZipCodeHidden()
                //.withSatelliteViewHidden()
                //.withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                //.withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .build(applicationContext)

            startActivityForResult(locationPickerIntent,MAP_BUTTON_REQUEST_CODE)

        }

    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                imgUri = result.uri
                img.setImageURI(imgUri)
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                val error: Exception = result.error
            }
        }else
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("RESULT****", "OK")
                if (requestCode == MAP_BUTTON_REQUEST_CODE) {
                    latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    data.getStringExtra(LOCATION_ADDRESS).let {

                        val s: String = it.substring(it.indexOf(",")+1)
                        val temp: String = s.substring(s.indexOf(",")+1)
                        temp.trim()

                        locationText.setText(temp)
                    }


                }
            }else
       if (resultCode == Activity.RESULT_CANCELED) {
           Log.d("RESULT****", "CANCELLED")
       }
    }

    private fun startDetailActivity(adData : HashMap<String, Any>, idHashMap : HashMap<String, Any>){

        val detailIntent: Intent = Intent(applicationContext,AdDetailActivity::class.java)
        detailIntent.putExtra(Constants.TITLE, adData[Constants.TITLE].toString())
        detailIntent.putExtra(Constants.DESCRIPTION,adData[Constants.DESCRIPTION].toString())
        detailIntent.putExtra(Constants.TIMESTAMP,"Just added")
        detailIntent.putExtra(Constants.CATEGORY,adData[Constants.CATEGORY].toString())
        detailIntent.putExtra(Constants.PAYMENT,adData[Constants.PAYMENT].toString())
        detailIntent.putExtra(Constants.IMAGE,adData[Constants.IMAGE].toString())
        detailIntent.putExtra(Constants.USER_ID,adData[Constants.USER_ID].toString())
        detailIntent.putExtra(Constants.LOCATION,adData[Constants.LOCATION].toString())
        detailIntent.putExtra(Constants.USER_ID,adData[Constants.USER_ID].toString())
        detailIntent.putExtra(Constants.POST_ID, idHashMap[Constants.POST_ID].toString())
        detailIntent.putExtra(Constants.USER_POST_ID, idHashMap[Constants.USER_POST_ID].toString())
        detailIntent.putExtra(Constants.IMAGE_NAME, adData["img_name"].toString())

        startActivity(detailIntent)
    }

    private fun isFieldCorrect(text: String, field: TextInputLayout): Boolean {
        when {
            text.isEmpty() -> {
                field.error = R.string.empty_field_error_message.toString()
                return false
            }
            else -> field.error = null
        }
        return true
    }

    private fun isFieldCorrect(text: String, field: TextInputLayout, counter: Int): Boolean {
        when {
            text.isEmpty() -> {
                field.error = R.string.empty_field_error_message.toString()
                return false
            }
            text.length > counter -> {
                field.error = R.string.text_length_error_message.toString()
                return false
            }
            else -> field.error = null
        }
        return true
    }

    private fun showSnackBar(activity: Activity, message: String, action: String? = null,
                             actionListener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, duration)
            snackBar.view.setBackgroundColor(Color.parseColor(Constants.SNACKBAR_COLOR))
            snackBar.setActionTextColor(Color.WHITE)
        if (action != null && actionListener!=null) {
            snackBar.setAction(action, actionListener)
        }
        snackBar.show()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            val fieldText = findViewById<TextInputLayout>(R.id.new_ad_payment_field)
            val fieldEdit = findViewById<TextInputEditText>(R.id.new_ad_payment_text)
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.new_ad_btn_money ->
                    if (checked) {
                        fieldText.hint = R.string.money_field_hint.toString()
                        fieldEdit.inputType = InputType.TYPE_CLASS_NUMBER
                        findViewById<ImageView>(R.id.new_ad_img_payment).setImageResource(R.drawable.ic_attach_money_black_24dp)
                    }
                R.id.new_ad_btn_sthelse ->
                    if (checked) {
                        findViewById<ImageView>(R.id.new_ad_img_payment).setImageResource(R.drawable.ic_cake_black_24dp)
                        fieldText.hint = R.string.other_field_hint.toString()
                    }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
