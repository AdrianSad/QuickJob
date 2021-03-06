package com.example.quickjob.Activities.EditActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class EditPaymentActivity : AppCompatActivity() {

    private lateinit var field: TextInputLayout
    private lateinit var textInput: TextInputEditText
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_payment)

        field = findViewById(R.id.change_payment_field)
        textInput = findViewById(R.id.change_payment_text)
        firestore = FirebaseFirestore.getInstance()
        textInput.setText(intent.extras.getString(Constants.PAYMENT))
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.change_payment_toolbar)
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

                val updateData = hashMapOf<String,Any>(Constants.PAYMENT to text)

                firestore.collection(Constants.POSTS_PATH).document(postID).update(updateData).addOnSuccessListener {
                    firestore.collection(Constants.USERS_PATH).document(user).collection(Constants.POSTS_PATH).document(userPostID).update(updateData).addOnCompleteListener { task ->

                        if(task.isSuccessful){
                            val updatedActivity = Intent(applicationContext, AdDetailActivity::class.java)
                            updatedActivity.putExtra(Constants.PAYMENT, text)
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

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            val fieldText = findViewById<TextInputLayout>(R.id.change_payment_field)
            val fieldEdit = findViewById<TextInputEditText>(R.id.change_payment_text)
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.change_payment_btn_money ->
                    if (checked) {
                        fieldText.hint = R.string.money_field_hint.toString()
                        fieldEdit.inputType = InputType.TYPE_CLASS_NUMBER
                        findViewById<ImageView>(R.id.change_payment_img).setImageResource(R.drawable.ic_attach_money_black_24dp)
                    }
                R.id.change_payment_btn_sthelse ->
                    if (checked) {
                        findViewById<ImageView>(R.id.change_payment_img).setImageResource(R.drawable.ic_cake_black_24dp)
                        fieldText.hint = R.string.other_field_hint.toString()
                    }
            }
        }
    }
}
