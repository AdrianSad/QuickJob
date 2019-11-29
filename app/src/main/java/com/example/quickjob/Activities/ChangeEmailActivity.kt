package com.example.quickjob.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ChangeEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        val firebaseAuth = FirebaseAuth.getInstance()

        val emailField = findViewById<TextInputLayout>(R.id.change_email_field)
        val emailText = findViewById<TextInputEditText>(R.id.change_email_text)

        val btn : CircularProgressButton = findViewById(R.id.change_email_btn)

        btn.setOnClickListener {

            btn.startAnimation()
            if(isEmailCorrect(emailText.text.toString(),emailField)){

                firebaseAuth.currentUser?.updateEmail(emailText.text.toString())
                    ?.addOnCompleteListener {

                        if(it.isSuccessful){
                            finish()
                        }else {
                            Toast.makeText(applicationContext,"Change email error : ${it.exception}", Toast.LENGTH_SHORT).show()
                            btn.revertAnimation()
                        }
                    }
            }
        }
    }

    private fun isEmailCorrect(email: String, registerEmailField: TextInputLayout): Boolean {
        when {
            email.isEmpty() -> {
                registerEmailField.error = "Field can't be empty!"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                registerEmailField.error = "Insert proper email address!"
                return false
            }
            else -> registerEmailField.error = null

        }
        return true
    }

}
