package com.example.quickjob.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ChangeEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_change_email)

        val firebaseAuth = FirebaseAuth.getInstance()

        val emailField = findViewById<TextInputLayout>(R.id.change_email_field)
        val emailText = findViewById<TextInputEditText>(R.id.change_email_text)
        findViewById<Toolbar>(R.id.change_title_toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
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
                registerEmailField.error = R.string.empty_field_error_message.toString()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
