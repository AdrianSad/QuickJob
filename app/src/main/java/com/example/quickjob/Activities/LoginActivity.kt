package com.example.quickjob.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: TextInputLayout
    private lateinit var passwordField: TextInputLayout
    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)



        val createAccBtn: Button = findViewById(R.id.login_btn_register)
        val forgotPassBtn: Button = findViewById(R.id.login_btn_forgot)
        val skipBtn: Button = findViewById(R.id.login_btn_skip)
        initVariables()

        createAccBtn.setOnClickListener {
            val register = Intent(applicationContext,RegisterActivity::class.java)
            startActivity(register)
        }

        skipBtn.setOnClickListener{
            val homeIntent = Intent(applicationContext,HomeActivity::class.java)
            startActivity(homeIntent)
        }

        /*forgotPassBtn.setOnClickListener {
            TODO: dodac aktywnosc
        }*/

        loginButton()
    }

    private fun loginButton() {
        val loginBtn: CircularProgressButton = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            loginBtn.startAnimation()

            if (isEmailCorrect(email, emailField) && isPasswordCorrect(password, passwordField)) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val home = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(home)
                    } else {
                        loginBtn.revertAnimation()
                    }
                }
            } else {
                loginBtn.revertAnimation()
            }
        }
    }

    private fun initVariables() {
        auth = FirebaseAuth.getInstance()
        emailField = findViewById(R.id.login_email_field)
        passwordField = findViewById(R.id.change_password_new_field)
        emailText = findViewById(R.id.login_email_field_text)
        passwordText = findViewById(R.id.change_password_new_text)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null){
            auth.signOut()
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

    private fun isEmailCorrect(email: String, registerEmailField: TextInputLayout): Boolean {
        when {
            email.isEmpty() -> {
                registerEmailField.error = R.string.empty_field_error_message.toString()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                registerEmailField.error = R.string.email_fiel_error_message.toString()
                return false
            }
            else -> registerEmailField.error = null

        }
        return true
    }
}


