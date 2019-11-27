package com.example.quickjob.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        val registerNameField: TextInputLayout = findViewById(R.id.register_name_field)
        val registerEmailField: TextInputLayout = findViewById(R.id.login_email_field)
        val registerPasswordField: TextInputLayout = findViewById(R.id.change_password_new_field)

        val registerNameText: TextInputEditText = findViewById(R.id.register_name_field_text)
        val registerEmailText: TextInputEditText = findViewById(R.id.login_email_field_text)
        val registerPasswordText: TextInputEditText = findViewById(R.id.change_password_new_text)

        val btn: Button = findViewById(R.id.register_btn)
        val progressBar: ProgressBar = findViewById(R.id.register_progressBar)

        auth = FirebaseAuth.getInstance()
        val firebaseFirestore = FirebaseFirestore.getInstance()

        val listener: View.OnClickListener = View.OnClickListener { _ ->

            val name = registerNameText.text.toString()
            val email = registerEmailText.text.toString()
            val password = registerPasswordText.text.toString()

            if(isNameCorrect(name,registerNameField) && isEmailCorrect(email, registerEmailField) && isPasswordCorrect(password,registerPasswordField)){

                btn.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

                    if(it.isSuccessful){

                        val user = auth.currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        val data : HashMap<String,Any> = hashMapOf("name" to name)
                        firebaseFirestore.collection("users").document(user?.uid.toString()).set(data).addOnSuccessListener{

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener{task ->

                            if(task.isSuccessful){

                                val loginIntent = Intent(applicationContext,LoginActivity::class.java)
                                startActivity(loginIntent)


                            }else{

                                Toast.makeText(applicationContext,"Register error : " + task.exception,Toast.LENGTH_SHORT).show()

                            }
                        }
                        }

                    }else{

                        Toast.makeText(applicationContext,"Register error : " + it.exception,Toast.LENGTH_SHORT).show()

                    }

                    btn.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }

            }
        }

        btn.setOnClickListener(listener)

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

    private fun isNameCorrect(text: String, field: TextInputLayout): Boolean {

        when {
            text.isEmpty() -> {
                field.error = "Field can't be empty!"
                return false
            }
            else -> field.error = null
        }
        return true

        //TODO: Dodac error czy jest juz w bazie danych
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null){
            auth.signOut()
        }

    }
}
