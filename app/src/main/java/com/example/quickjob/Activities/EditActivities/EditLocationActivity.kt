package com.example.quickjob.Activities.EditActivities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import com.theartofdev.edmodo.cropper.CropImage
import java.lang.Exception

class EditLocationActivity : AppCompatActivity() {

    private lateinit var field: TextInputLayout
    private lateinit var textInput: TextInputEditText
    private lateinit var firestore: FirebaseFirestore
    private val MAP_BUTTON_REQUEST_CODE = 3
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)

        field = findViewById(R.id.change_location_field)
        textInput = findViewById(R.id.change_location_text)
        firestore = FirebaseFirestore.getInstance()
        textInput.setText(intent.extras.getString(Constants.LOCATION))
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.change_location_toolbar)
        setSupportActionBar(toolbar)


        getLocation()
    }

    private fun getLocation(){
        val locationImg : ImageView = findViewById(R.id.change_location_img)
        locationImg.setOnClickListener {

            val postID = intent.extras.getString(Constants.POST_ID)
            var location: LatLng? = null
            firestore.collection(Constants.POSTS_PATH).document(postID).get().addOnSuccessListener {

                if (it != null) {
                    val obj = it.data?.getValue(Constants.LATITUDE_LONGITUDE) as HashMap<*, *>
                    location = LatLng(obj["latitude"] as Double, obj["longitude"] as Double)

                    var locationPickerIntent: Intent
                    if (location != null) {
                        locationPickerIntent = LocationPickerActivity.Builder()
                            .withGeolocApiKey(R.string.google_maps_key.toString())
                            .withLocation(location)
                            .withDefaultLocaleSearchZone()
                            .withGoogleTimeZoneEnabled()
                            .withUnnamedRoadHidden()
                            .build(applicationContext)

                    } else {
                        locationPickerIntent = LocationPickerActivity.Builder()
                            .withGeolocApiKey(R.string.google_maps_key.toString())
                            .withDefaultLocaleSearchZone()
                            .withGoogleTimeZoneEnabled()
                            .withUnnamedRoadHidden()
                            .build(applicationContext)
                    }
                    startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_change_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item?.itemId == R.id.change_menu_submit){

            val text = textInput.text.toString()
            val postID = intent.extras.getString(Constants.POST_ID)
            val user = intent.extras.getString(Constants.USER_ID)
            val userPostID = intent.extras.getString(Constants.USER_POST_ID)

            if(isFieldCorrect(text, field) && longitude != 0.0 && latitude != 0.0){

                val location: LatLng = LatLng(latitude,longitude)
                val updateData = hashMapOf<String,Any>(
                    Constants.LOCATION to text,
                    Constants.LATITUDE_LONGITUDE to location)

                firestore.collection(Constants.POSTS_PATH).document(postID).update(updateData).addOnSuccessListener {
                    firestore.collection(Constants.USERS_PATH).document(user).collection(Constants.POSTS_PATH).document(userPostID).update(updateData).addOnCompleteListener { task ->

                        if(task.isSuccessful){
                            val updatedActivity = Intent(applicationContext, AdDetailActivity::class.java)
                            updatedActivity.putExtra(Constants.LOCATION, text)
                            finish()
                        }
                    }
                }
            }

            true
        }
        else false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("RESULT****", "OK")
                if (requestCode == MAP_BUTTON_REQUEST_CODE) {
                    latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    data.getStringExtra(LOCATION_ADDRESS).let {

                        val s: String = it.substring(it.indexOf(",")+1)
                        val temp: String = s.substring(s.indexOf(",")+1)
                        temp.trim()

                        textInput.setText(temp)
                    }


                }
            }else
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("RESULT****", "CANCELLED")
                }
    }
}
