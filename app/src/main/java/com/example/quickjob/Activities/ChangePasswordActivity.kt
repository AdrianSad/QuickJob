package com.example.quickjob.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_change_password)

        val firebaseAuth = FirebaseAuth.getInstance()

        val presentField = findViewById<TextInputLayout>(R.id.change_password_present_field)
        val presentText = findViewById<TextInputEditText>(R.id.change_password_present_text)
        val newField = findViewById<TextInputLayout>(R.id.change_password_new_field)
        val newText = findViewById<TextInputEditText>(R.id.change_password_new_text)
        val confField = findViewById<TextInputLayout>(R.id.change_password_conf_field)
        val confText = findViewById<TextInputEditText>(R.id.change_password_conf_text)

        val btn : CircularProgressButton = findViewById(R.id.change_pass_btn)

        findViewById<Toolbar>(R.id.change_password_toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        btn.setOnClickListener {
            btn.startAnimation()
            if(isPasswordCorrect(presentText.text.toString(),presentField) && isPasswordCorrect(newText.text.toString(),newField) && isPasswordCorrect(confText.text.toString(),confField)){

                if(newText.text == confText.text) {

                    firebaseAuth.currentUser?.updatePassword(newText.text.toString())
                        ?.addOnCompleteListener {

                            if (it.isSuccessful) {
                                finish()
                            } else {
                                btn.revertAnimation()

                            }
                        }
                } else {

                    confField.error = R.string.password_diff_error_message.toString()
                    btn.revertAnimation()

                }

            }
        }
    }

    private fun isPasswordCorrect(password: String, registerPasswordField: TextInputLayout): Boolean {
        when {
            password.isEmpty() -> {
                registerPasswordField.error = R.string.empty_field_error_message.toString()
                return false
            }
            password.length < 4 -> {
                registerPasswordField.error = R.string.password_field_error_message.toString()
                return false
            }
            else -> registerPasswordField.error = null
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
