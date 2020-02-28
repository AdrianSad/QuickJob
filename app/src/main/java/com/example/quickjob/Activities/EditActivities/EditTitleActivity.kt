package com.example.quickjob.Activities.EditActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class EditTitleActivity : AppCompatActivity() {

    private lateinit var field: TextInputLayout
    private lateinit var textInput: TextInputEditText
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_title)

        field = findViewById(R.id.change_desc_field)
        textInput = findViewById(R.id.change_desc_text)
        firestore = FirebaseFirestore.getInstance()
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.change_title_toolbar)
        textInput.setText(intent.extras.getString(Constants.TITLE))

        setSupportActionBar(toolbar)
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

            if(isFieldCorrect(text, field)){

                val updateData = hashMapOf<String,Any>(Constants.TITLE to text)

                firestore.collection(Constants.POSTS_PATH).document(postID).update(updateData).addOnSuccessListener {
                    firestore.collection(Constants.USERS_PATH).document(user).collection(Constants.POSTS_PATH).document(userPostID).update(updateData).addOnCompleteListener { task ->

                        if(task.isSuccessful){
                            val intent = Intent(applicationContext, AdDetailActivity::class.java)
                            intent.putExtra(Constants.TITLE, text)
                            setResult(RESULT_OK, intent)
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
            text.length > 25 -> {
                field.error = R.string.title_field_error_message.toString()
                return false
            }
            else -> field.error = null
        }
        return true
    }
}
