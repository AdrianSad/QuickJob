package com.example.quickjob.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import android.text.Editable as Editable

class SetupActivity : AppCompatActivity() {

    private var imgUri: Uri = Uri.EMPTY
    private lateinit var img: CircleImageView
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_setup)

        img = findViewById(R.id.setup_change_img)
        val nameField: TextInputLayout = findViewById(R.id.setup_change_name_field)
        val nameText: TextInputEditText = findViewById(R.id.setup_change_name_text)
        val descField: TextInputLayout = findViewById(R.id.setup_change_desc_field)
        val descText: TextInputEditText = findViewById(R.id.setup_change_desc_text)
        val submitBtn: CircularProgressButton = findViewById(R.id.setup_change_submit_btn)
        val passBtn: Button = findViewById(R.id.setup_change_pass_btn)
        val emailBtn: Button = findViewById(R.id.setup_change_email_btn)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val currentUser = firebaseAuth.currentUser
        //TODO: reset pass btn
        findViewById<Toolbar>(R.id.setup_toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }


        val user = currentUser

        if(user!!.photoUrl != null){
            Glide.with(this).load(currentUser.photoUrl)
                .into(img)
        }

        firebaseFirestore.collection(Constants.USERS_PATH).document(currentUser.uid).get().addOnSuccessListener {
            if(it.data!!.containsKey(Constants.DESCRIPTION)){
                descText.setText(it.data!!.getValue(Constants.DESCRIPTION).toString(),TextView.BufferType.EDITABLE)
            }
        }

        nameText.setText(user.displayName.toString(),TextView.BufferType.EDITABLE)

        submitBtn.setOnClickListener { view ->
            submitBtn.startAnimation()

            if(isNameCorrect(nameText.text.toString(),nameField) && isDescCorrect(descText.text.toString(),descField)){
                val data: HashMap<String, Any> = hashMapOf(
                    Constants.DESCRIPTION to descText.text.toString(),
                    Constants.USER_NAME to nameText.text.toString())

                if(imgUri != Uri.EMPTY){
                    val storageReference =FirebaseStorage.getInstance().reference.child("user_images")
                    val imageFilePath = storageReference.child(imgUri.lastPathSegment)
                    imageFilePath.putFile(imgUri).addOnSuccessListener {
                        imageFilePath.downloadUrl.addOnSuccessListener { uri ->

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(nameText.text.toString())
                                .setPhotoUri(uri)
                                .build()

                            data.put(Constants.IMAGE,uri.toString())
                            
                            firebaseFirestore.collection(Constants.USERS_PATH).document(currentUser.uid).update(data).addOnSuccessListener {
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener {task ->

                                if(task.isSuccessful){
                                    finish()
                                }else{
                                    submitBtn.revertAnimation()
                                }
                            }
                            }.addOnFailureListener {
                                submitBtn.revertAnimation()
                            }
                        }
                    }


                } else {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nameText.text.toString())
                            .build()

                    firebaseFirestore.collection(Constants.USERS_PATH).document(currentUser.uid).update(data).addOnSuccessListener {

                        currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                finish()
                            } else {
                                submitBtn.revertAnimation()
                            }
                        }

                    }.addOnFailureListener {
                        submitBtn.revertAnimation()
                    }
                }
            }else{
                submitBtn.revertAnimation()
            }
        }

        img.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(96, 96)
                .setAspectRatio(1, 1)
                .start(this)
        }

        emailBtn.setOnClickListener {
            val emailIntent = Intent(applicationContext, ChangeEmailActivity::class.java)
            startActivity(emailIntent)
        }

        passBtn.setOnClickListener {
            val passIntent = Intent(applicationContext, ChangePasswordActivity::class.java)
            startActivity(passIntent)
        }

    }

    private fun isDescCorrect(desc: String, descField: TextInputLayout): Boolean {
        when {
            desc.isEmpty() -> {
                descField.error = R.string.empty_field_error_message.toString()
                return false
            }
            desc.length > 100 -> {
                descField.error = R.string.text_length_error_message.toString()
                return false
            }
            else -> descField.error = null
        }
        return true
    }


    private fun isNameCorrect(text: String, field: TextInputLayout): Boolean {

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
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                imgUri = result.uri
                img.setImageURI(imgUri)
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                val error: Exception = result.error
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
