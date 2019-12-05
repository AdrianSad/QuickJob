package com.example.quickjob.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.android.gms.common.api.Status
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
import com.schibstedspain.leku.locale.SearchZoneRect
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class NewAdActivity : AppCompatActivity() {

    lateinit var img: ImageView
    lateinit var imgUri: Uri
    private val MAP_BUTTON_REQUEST_CODE = 3
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var locationText : TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val firebaseStorage = FirebaseStorage.getInstance()
        val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser


        img.setOnClickListener{

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(410,200)
                .setAspectRatio(2,1)
                .start(this)
        }

        doneBtn.setOnClickListener{

            doneBtn.startAnimation()

            val title: String = titleText.text.toString()
            val location: String = locationText.text.toString()
            val desc: String = descText.text.toString()
            val payment: String = paymentText.text.toString()

            if(isFieldCorrect(title,titleField) && isFieldCorrect(location,locationField) && isDescCorrect(desc,descField) && isFieldCorrect(payment, paymentField)){

                if(imgUri == null){

                    showSnackBar(this,"You have to add a image!")
                    //doneBtn.doneLoadingAnimation(Color.parseColor("#FF5555"),BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_close_white_24dp))
                    doneBtn.revertAnimation()


                }else {

                    val randomName = UUID.randomUUID().toString()
                    val imgReference = firebaseStorage.reference.child("ad_images").child("$randomName.jpg")
                    var uploadTask = imgReference.putFile(imgUri)
                    val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>  { task ->
                        if(!task.isSuccessful){
                            task.exception?.let {
                                //doneBtn.doneLoadingAnimation(Color.parseColor("#FF5555"),BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_close_white_24dp))
                                doneBtn.revertAnimation()

                                throw it
                            }
                        }
                        return@Continuation imgReference.downloadUrl
                    }).addOnCompleteListener {

                        if(it.isSuccessful){
                            val downloadUrl = it.result.toString()

                            val adData = hashMapOf<String,Any>(
                                "title" to title,
                                "desc" to desc,
                                "time" to FieldValue.serverTimestamp(),
                                "img" to downloadUrl,
                                "location" to location,
                                "category" to category.selectedItem.toString(),
                                "payment" to payment,
                                "user" to currentUser!!.uid
                            )

                            if(longitude != 0.0 && latitude != 0.0){
                                val location: LatLng = LatLng(latitude,longitude)
                                adData.put("latlng",location)
                            }

                            firebaseFirestore.collection("posts").add(adData).addOnSuccessListener { document ->

                                adData.put("id", document.id)

                                    firebaseFirestore.collection("users").document(currentUser.uid).collection("posts").add(adData).addOnCompleteListener { task ->
                                        if(task.isSuccessful)
                                            //doneBtn.doneLoadingAnimation(Color.parseColor("#8BC34A"),BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_done_white_24dp))
                                            startDetailActivity(adData)
                                            finish()
                                    }
                                }.addOnFailureListener{ exception ->
                                Toast.makeText(applicationContext,"Adding a post error : $exception",Toast.LENGTH_LONG).show()
                                //doneBtn.doneLoadingAnimation(Color.parseColor("#FF5555"),BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_close_white_24dp))
                                doneBtn.revertAnimation()

                            }


                        }else {
                            //doneBtn.doneLoadingAnimation(Color.parseColor("#FF5555"),BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_close_white_24dp))
                            doneBtn.revertAnimation()
                        }
                    }

                }
            }else {
                //doneBtn.doneLoadingAnimation(Color.parseColor("#FF5555"),(drawable))
                doneBtn.revertAnimation()
            }

        }

        locationImg.setOnClickListener {

            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(52.2297259,21.0116312)
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
                Toast.makeText(applicationContext,"Crop image error : $error",Toast.LENGTH_SHORT).show()
            }
        }else
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("RESULT****", "OK")
                if (requestCode == MAP_BUTTON_REQUEST_CODE) {
                    latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    locationText.setText(address)

                }
            }else
       if (resultCode == Activity.RESULT_CANCELED) {
           Log.d("RESULT****", "CANCELLED")
       }
    }

    private fun startDetailActivity(adData : HashMap<String, Any>){

        var miliseconds: Long = 0
        try {
            miliseconds = adData["time"] as Long
        }catch (e: Exception){
            e.printStackTrace()
        }

        val detailIntent: Intent = Intent(applicationContext,AdDetailActivity::class.java)
        detailIntent.putExtra("title", adData["title"].toString())
        detailIntent.putExtra("desc",adData["desc"].toString())
        detailIntent.putExtra("timestamp",miliseconds)
        detailIntent.putExtra("category",adData["cat"].toString())
        detailIntent.putExtra("payment",adData["payment"].toString())
        detailIntent.putExtra("img",adData["img"].toString())
        detailIntent.putExtra("user",adData["user"].toString())
        detailIntent.putExtra("location",adData["location"].toString())

        startActivity(detailIntent)
    }

    private fun isFieldCorrect(text: String, field: TextInputLayout): Boolean {
        when {
            text.isEmpty() -> {
                field.error = "Field can't be empty!"
                return false
            }
            else -> field.error = null
        }
        return true
    }

    private fun isDescCorrect(text: String, field: TextInputLayout): Boolean {
        when {
            text.isEmpty() -> {
                field.error = "Field can't be empty!"
                return false
            }
            text.length > 100 -> {
                field.error = "Description is too long!"
                return false
            }
            else -> field.error = null
        }
        return true
    }

    private fun showSnackBar(activity: Activity, message: String, action: String? = null,
                             actionListener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, duration)
            snackBar.view.setBackgroundColor(Color.parseColor("#CC000000")) // todo update your color
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
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.new_ad_btn_money ->
                    if (checked) {
                        fieldText.hint = "Write how much money are you able to spend"
                        findViewById<ImageView>(R.id.new_ad_img_payment).setImageResource(R.drawable.ic_attach_money_black_24dp)
                    }
                R.id.new_ad_btn_sthelse ->
                    if (checked) {
                        findViewById<ImageView>(R.id.new_ad_img_payment).setImageResource(R.drawable.ic_cake_black_24dp)
                        fieldText.hint = "How would you like to reward other person ?"
                    }
            }
        }
    }
}
