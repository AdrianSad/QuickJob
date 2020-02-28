package com.example.quickjob.Activities

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fabBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_home)

        createFab()
        mAuth = FirebaseAuth.getInstance()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val currentUser = mAuth.currentUser


        setSupportActionBar(toolbar)
        refreshNavHeader(navView)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_map,
                R.id.nav_tools, R.id.nav_share, R.id.nav_setup, R.id.nav_logout
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        if(currentUser == null) {
            val loginItem: MenuItem = navView.menu.findItem(R.id.nav_logout)
            loginItem.title = R.string.sign_in.toString()

            val profileItem: MenuItem = navView.menu.findItem(R.id.nav_setup)
            profileItem.isVisible = false

        }
    }
    private fun createFab(){
        fabBtn= findViewById(R.id.fab)
        fabBtn.setOnClickListener {
            if(mAuth.currentUser != null) {
                val newAdIntent = Intent(applicationContext, NewAdActivity::class.java)
                startActivity(
                    newAdIntent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
            }else {
                showSnackBar(this,R.string.not_logged_in.toString())
            }
        }
    }

    private fun refreshNavHeader(navView: NavigationView) {
        val headerView: View = navView.getHeaderView(0)
        val userName:TextView = headerView.findViewById(R.id.nav_user_name)
        val userEmail: TextView = headerView.findViewById(R.id.nav_user_email)
        val userImage: CircleImageView = headerView.findViewById(R.id.nav_userimage)
        val currentuser = mAuth.currentUser

        if(currentuser != null) {
            userName.text = currentuser.displayName
            userEmail.text = currentuser.email
            if(currentuser.photoUrl != null){
                Glide.with(this).load(currentuser.photoUrl).placeholder(R.drawable.default_avatar)
                    .into(userImage)
            }else {
                Glide.with(this).load(R.drawable.default_avatar)
                    .into(userImage)
            }
        }else{

            userName.text = R.string.log_in_hint.toString()
            userEmail.text = ""
            Glide.with(this).load(R.drawable.default_avatar)
                .into(userImage)

            userName.setOnClickListener {
                val login = Intent(applicationContext,LoginActivity::class.java)
                startActivity(login)
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val navView: NavigationView = findViewById(R.id.nav_view)
        refreshNavHeader(navView)
    }

    private fun showSnackBar(activity: Activity, message: String, action: String? = null,
                             actionListener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, duration)
        snackBar.view.setBackgroundColor(Color.parseColor(Constants.SNACKBAR_COLOR))
        snackBar.setActionTextColor(Color.WHITE)
        if (action != null && actionListener!=null) {
            snackBar.setAction(action, actionListener)
        }
        snackBar.show()
    }

    public fun signOut(view: View){
        if(mAuth.currentUser != null){
            mAuth.signOut()
        }
        val newAdIntent = Intent(applicationContext,LoginActivity::class.java)
        startActivity(newAdIntent)

    }

    fun hideFloatingActionButton(){
        fabBtn.hide()
    }

    fun showFloatingActionButton(){
        fabBtn.show()
    }
}
