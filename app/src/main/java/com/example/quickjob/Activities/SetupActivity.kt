package com.example.quickjob.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    lateinit var img: CircleImageView
    lateinit var firebaseFirestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val user = currentUser

        if(user!!.photoUrl != null){
            Glide.with(this).load(currentUser.photoUrl)
                .into(img)
        }

        firebaseFirestore.collection("users").document(currentUser.uid).get().addOnSuccessListener {
            if(it.data!!.containsKey("desc")){
                descText.setText(it.data!!.getValue("desc").toString(),TextView.BufferType.EDITABLE)
            }
        }

        nameText.setText(user.displayName.toString(),TextView.BufferType.EDITABLE)

        submitBtn.setOnClickListener { view ->

            submitBtn.startAnimation()

            if(isNameCorrect(nameText.text.toString(),nameField) && isDescCorrect(descText.text.toString(),descField)){

                val data: HashMap<String, Any> = hashMapOf(
                    "desc" to descText.text.toString(),
                    "name" to nameText.text.toString())

                if(imgUri != Uri.EMPTY){

                    val storageReference =FirebaseStorage.getInstance().reference.child("user_images")
                    val imageFilePath = storageReference.child(imgUri.lastPathSegment)
                    imageFilePath.putFile(imgUri).addOnSuccessListener {
                        imageFilePath.downloadUrl.addOnSuccessListener { uri ->

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(nameText.text.toString())
                                .setPhotoUri(uri)
                                .build()

                            data.put("img",uri.toString())
                            
                            firebaseFirestore.collection("users").document(currentUser.uid).update(data).addOnSuccessListener {
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener {task ->

                                if(task.isSuccessful){
                                    finish()
                                }else{
                                    Toast.makeText(applicationContext,"Update with image error : ${task.exception}", Toast.LENGTH_SHORT).show()
                                    submitBtn.revertAnimation()

                                }
                            }
                            }.addOnFailureListener {
                                Toast.makeText(applicationContext,"Desc error : $it", Toast.LENGTH_SHORT).show()
                                submitBtn.revertAnimation()

                            }
                        }
                    }


                } else {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nameText.text.toString())
                            .build()

                    firebaseFirestore.collection("users").document(currentUser.uid).update(data).addOnSuccessListener {

                        currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Update error : ${task.exception}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                submitBtn.revertAnimation()
                            }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(applicationContext,"Desc error : $it", Toast.LENGTH_SHORT).show()
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
                descField.error = "Field can't be empty!"
                return false
            }
            desc.length > 100 -> {
                descField.error = "Too many letters!"
                return false
            }
            else -> descField.error = null

        }
        return true
    }


    private fun isNameCorrect(text: String, field: TextInputLayout): Boolean {

        when {
            text.isEmpty() -> {
                field.error = "Field can't be empty!"
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
                Toast.makeText(applicationContext,"Crop image error : $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
