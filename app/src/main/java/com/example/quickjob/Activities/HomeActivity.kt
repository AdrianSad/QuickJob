package com.example.quickjob.Activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.Gravity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_setup.*

class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

// Check if we're running on Android 5.0 or higher
    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(window){
                exitTransition = Explode()
            }
        } else {
            // Swap without transition
        }*/

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val newAdIntent = Intent(applicationContext,NewAdActivity::class.java)
            startActivity(newAdIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        mAuth = FirebaseAuth.getInstance()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        refreshNavHeader(navView)

        navView.setNavigationItemSelectedListener(this)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_map,
                R.id.nav_tools, R.id.nav_share, R.id.nav_setup, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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

            userName.text = "Log in to unblock new things!"
            userEmail.text = ""
            Glide.with(this).load(R.drawable.default_avatar)
                .into(userImage)

            userName.setOnClickListener {
                val login = Intent(applicationContext,LoginActivity::class.java)
                startActivity(login)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        if(p0.itemId == R.id.nav_logout){

            if(mAuth.currentUser != null){
                mAuth.signOut()
                Toast.makeText(applicationContext,"Log out",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext,"First Log In",Toast.LENGTH_LONG).show()
                val newAdIntent = Intent(applicationContext,NewAdActivity::class.java)
                startActivity(newAdIntent)
            }

        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val navView: NavigationView = findViewById(R.id.nav_view)
        refreshNavHeader(navView)
    }

/*override fun onStart() {
    super.onStart()

    val currentUser = mAuth.currentUser

    if(currentUser == null){
        val login = Intent(applicationContext,LoginActivity::class.java)
        startActivity(login)
    }
}*/


}