package com.example.quickjob.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val firebaseAuth = FirebaseAuth.getInstance()

        val presentField = findViewById<TextInputLayout>(R.id.change_password_present_field)
        val presentText = findViewById<TextInputEditText>(R.id.change_password_present_text)
        val newField = findViewById<TextInputLayout>(R.id.change_password_new_field)
        val newText = findViewById<TextInputEditText>(R.id.change_password_new_text)
        val confField = findViewById<TextInputLayout>(R.id.change_password_conf_field)
        val confText = findViewById<TextInputEditText>(R.id.change_password_conf_text)

        val btn : Button = findViewById(R.id.change_pass_btn)

        btn.setOnClickListener {

            if(isPasswordCorrect(presentText.text.toString(),presentField) && isPasswordCorrect(newText.text.toString(),newField) && isPasswordCorrect(confText.text.toString(),confField)){

                if(newText.text == confText.text) {

                    firebaseAuth.currentUser?.updatePassword(newText.text.toString())
                        ?.addOnCompleteListener {

                            if (it.isSuccessful) {
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Change password error : ${it.exception}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                } else {

                    confField.error = "Passwords must be the same"

                }

            }
        }
    }

    private fun isPasswordCorrect(password: String, registerPasswordField: TextInputLayout): Boolean {
        when {
            password.isEmpty() -> {
                registerPasswordField.error = "Field can't be empty!"
                return false
            }
            password.length < 4 -> {
                registerPasswordField.error = "Password is too short!"
                return false
            }
            else -> registerPasswordField.error = null
        }
        return true
    }
}
