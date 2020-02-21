package com.example.quickjob.Activities.EditActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Spinner
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.firebase.firestore.FirebaseFirestore

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.change_cat_toolbar)
        setSupportActionBar(toolbar)
        firestore = FirebaseFirestore.getInstance()
        spinner = findViewById(R.id.edit_category_spinner)
        spinner.setSelection(resources.getStringArray(R.array.categories).indexOf(intent.extras.getString(Constants.CATEGORY)))



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_change_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item?.itemId == R.id.change_menu_submit){

            val text = spinner.selectedItem.toString()
            val postID = intent.extras.getString(Constants.POST_ID)
            val user = intent.extras.getString(Constants.USER_ID)
            val userPostID = intent.extras.getString(Constants.USER_POST_ID)

                val updateData = hashMapOf<String,Any>(Constants.CATEGORY to text)

                firestore.collection(Constants.POSTS_PATH).document(postID).update(updateData).addOnSuccessListener {
                    firestore.collection(Constants.USERS_PATH).document(user).collection(Constants.POSTS_PATH).document(userPostID).update(updateData).addOnCompleteListener { task ->

                        if(task.isSuccessful){
                            val updatedActivity = Intent(applicationContext, AdDetailActivity::class.java)
                            updatedActivity.putExtra(Constants.CATEGORY, text)
                            finish()
                        }
                    }
                }


            true
        }
        else false
    }
}
