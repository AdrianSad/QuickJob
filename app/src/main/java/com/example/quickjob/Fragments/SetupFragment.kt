package com.example.quickjob.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quickjob.Activities.HomeActivity
import com.example.quickjob.Activities.SetupActivity
import com.example.quickjob.Adapters.AdViewAdapter
import com.example.quickjob.Adapters.ProfileAdViewAdapter
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.ConstantValues.Constants

import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class SetupFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var desc: TextView
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var img: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private  var arr = arrayListOf<Advertisement>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_setup, container, false)

        initVariables(view)
        initializeList(view)

        return view
    }

    private fun initVariables(view: View) {
        img = view.findViewById(R.id.setup_img)
        name = view.findViewById(R.id.setup_name)
        email = view.findViewById(R.id.setup_email)
        desc = view.findViewById(R.id.setup_desc)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        viewManager = LinearLayoutManager(view.context)
        viewAdapter = ProfileAdViewAdapter(view.context, arr)
        (activity as HomeActivity).hideFloatingActionButton()

        recyclerView = view.findViewById<RecyclerView>(R.id.setup_recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val editBtn = view.findViewById<Button>(R.id.setup_edit_btn)
        editBtn.setOnClickListener {
            val setupIntent : Intent = Intent(view.context,SetupActivity::class.java)
            startActivity(setupIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            if(currentUser.photoUrl != null){
                Glide.with(this).load(currentUser.photoUrl).placeholder(R.drawable.default_avatar)
                    .into(img)
            }
            name.text = currentUser.displayName
            email.text = currentUser.email

            firebaseFirestore.collection(Constants.USERS_PATH).document(currentUser.uid).get().addOnSuccessListener {

                if(it != null) {
                    if(it.data?.getValue(Constants.DESCRIPTION) != null){
                        desc.text = it.data!!.getValue(Constants.DESCRIPTION).toString()
                    }
                }
            }
        }
    }

    private fun initializeList(view : View){
        firebaseFirestore.collection(Constants.USERS_PATH).document(firebaseAuth.currentUser?.uid.toString()).collection(Constants.POSTS_PATH).get().addOnSuccessListener { documents ->

            for(document in documents){

                val item = document.toObject(Advertisement::class.java)
                arr.add(item)
                viewAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(view.context,"Retrieve post error : $it", Toast.LENGTH_LONG).show()
        }
    }

}
