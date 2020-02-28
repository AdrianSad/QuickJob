package com.example.quickjob.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.quickjob.ConstantValues.Constants
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

        val btn: CircularProgressButton = findViewById(R.id.register_btn)
        val loginBtn : Button = findViewById(R.id.register_registered_btn)

        auth = FirebaseAuth.getInstance()
        val firebaseFirestore = FirebaseFirestore.getInstance()

        val listener: View.OnClickListener = View.OnClickListener { _ ->

            btn.startAnimation()

            val name = registerNameText.text.toString()
            val email = registerEmailText.text.toString()
            val password = registerPasswordText.text.toString()

            if(isNameCorrect(name,registerNameField) && isEmailCorrect(email, registerEmailField) && isPasswordCorrect(password,registerPasswordField)){

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

                    if(it.isSuccessful){
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        val data : HashMap<String,Any> = hashMapOf(Constants.USER_NAME to name)
                        firebaseFirestore.collection(Constants.USERS_PATH).document(user?.uid.toString()).set(data).addOnSuccessListener{
                            user?.updateProfile(profileUpdates)?.addOnCompleteListener{task ->

                            if(task.isSuccessful){
                                val loginIntent = Intent(applicationContext,LoginActivity::class.java)
                                startActivity(loginIntent)
                            }else{
                                Toast.makeText(applicationContext,"Register error : " + task.exception,Toast.LENGTH_SHORT).show()
                                btn.revertAnimation()
                            }
                        }
                        }
                    }else{
                        Toast.makeText(applicationContext,"Register error : " + it.exception,Toast.LENGTH_SHORT).show()
                        btn.revertAnimation()
                    }
                }
            }else{
                btn.revertAnimation()
            }
        }
        btn.setOnClickListener(listener)
        loginBtn.setOnClickListener {
            val loginIntent: Intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
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

    private fun isNameCorrect(text: String, field: TextInputLayout): Boolean {

        when {
            text.isEmpty() -> {
                field.error = R.string.empty_field_error_message.toString()
                return false
            }
            else -> field.error = null
        }
        return true
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null){
            auth.signOut()
        }

    }
}
