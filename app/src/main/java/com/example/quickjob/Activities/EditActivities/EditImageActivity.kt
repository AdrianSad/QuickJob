package com.example.quickjob.Activities.EditActivities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.Exception
import java.util.*

class EditImageActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage : FirebaseStorage
    private lateinit var imgUri: Uri
    private lateinit var img : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.change_image_toolbar)
        setSupportActionBar(toolbar)
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        loadImage()
    }

    private fun loadImage() {
        val adImg: String = intent.extras.getString(Constants.IMAGE)
        img = findViewById(R.id.edit_image_img)
        Glide.with(this).load(adImg).into(img)

        // IMAGE PICKER
        img.setOnClickListener {

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(410, 200)
                .setAspectRatio(2, 1)
                .start(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_change_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item?.itemId == R.id.change_menu_submit) {

            val postID = intent.extras.getString(Constants.POST_ID)
            val user = intent.extras.getString(Constants.USER_ID)
            val userPostID = intent.extras.getString(Constants.USER_POST_ID)
            val imgName = intent.extras.getString(Constants.IMAGE_NAME)

            if (imgUri == null) {
                showSnackBar(this, "You have to add a image!")
            } else {

            val randomName = UUID.randomUUID().toString()
            val imgReference = firebaseStorage.reference.child(Constants.IMAGES_PATH).child("$randomName.jpg")
            var uploadTask = imgReference.putFile(imgUri)
            val urlTask =
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                    if (!task.isSuccessful) {
                        task.exception?.let {

                            throw it
                        }
                    }
                    return@Continuation imgReference.downloadUrl
                }).addOnCompleteListener {

                    if (it.isSuccessful) {
                        val downloadUrl = it.result.toString()
                        val updateData = hashMapOf<String, Any>(Constants.IMAGE to downloadUrl)

                        val deleteReference = firebaseStorage.reference.child(Constants.IMAGES_PATH).child(imgName)
                            deleteReference.delete().addOnCompleteListener { deleteTask ->

                                if (deleteTask.isSuccessful) {

                                    firestore.collection(Constants.POSTS_PATH).document(postID)
                                        .update(updateData).addOnSuccessListener {
                                        firestore.collection(Constants.USERS_PATH).document(user)
                                            .collection(Constants.POSTS_PATH).document(userPostID)
                                            .update(updateData).addOnCompleteListener { task ->

                                            if (task.isSuccessful) {
                                                val updatedActivity = Intent(
                                                    applicationContext, AdDetailActivity::class.java
                                                )
                                                updatedActivity.putExtra(Constants.IMAGE, downloadUrl)
                                                finish()
                                            }
                                        }
                                    }

                                }
                            }
                    }
                }
        }

            true
        }
        else false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                imgUri = result.uri
                img.setImageURI(imgUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                val error: Exception = result.error
                Toast.makeText(applicationContext, "Crop image error : $error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
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
}
