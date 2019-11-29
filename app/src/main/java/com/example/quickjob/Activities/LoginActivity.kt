package com.example.quickjob.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var emailField: TextInputLayout
    lateinit var passwordField: TextInputLayout
    lateinit var emailText: TextInputEditText
    lateinit var passwordText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val createAccBtn: Button = findViewById(R.id.login_btn_register)
        val loginBtn: CircularProgressButton = findViewById(R.id.login_btn)
        val forgotPassBtn: Button = findViewById(R.id.login_btn_forgot)
        val skipBtn: Button = findViewById(R.id.login_btn_skip)

        emailField = findViewById(R.id.login_email_field)
        passwordField = findViewById(R.id.change_password_new_field)

        emailText = findViewById(R.id.login_email_field_text)
        passwordText = findViewById(R.id.change_password_new_text)


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

        loginBtn.setOnClickListener {

            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            loginBtn.startAnimation()

            if(isEmailCorrect(email,emailField) && isPasswordCorrect(password,passwordField)){

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {

                    if(it.isSuccessful){

                        val home = Intent(applicationContext,HomeActivity::class.java)
                        startActivity(home)

                    }else {

                        Toast.makeText(applicationContext,"Login error : " + it.exception,
                            Toast.LENGTH_SHORT).show()
                        loginBtn.revertAnimation()

                    }


                }

            }else {
                loginBtn.revertAnimation()
            }

        }


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


