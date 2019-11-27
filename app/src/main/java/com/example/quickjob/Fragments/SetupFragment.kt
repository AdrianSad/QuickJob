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
import com.example.quickjob.Activities.SetupActivity
import com.example.quickjob.Adapters.AdViewAdapter
import com.example.quickjob.Adapters.ProfileAdViewAdapter
import com.example.quickjob.Classes.Advertisement

import com.example.quickjob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class SetupFragment : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var desc: TextView
    lateinit var email: TextView
    lateinit var name: TextView
    lateinit var img: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private  var arr = arrayListOf<Advertisement>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_setup, container, false)
        img = view.findViewById(R.id.setup_img)
        val editBtn = view.findViewById<Button>(R.id.setup_edit_btn)
        name = view.findViewById(R.id.setup_name)
        email= view.findViewById(R.id.setup_email)
        desc = view.findViewById(R.id.setup_desc)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = ProfileAdViewAdapter(view.context,arr)

        recyclerView = view.findViewById<RecyclerView>(R.id.setup_recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.uid.toString()).collection("posts").get().addOnSuccessListener { documents ->

            for(document in documents){

                val item = document.toObject(Advertisement::class.java)
                arr.add(item)
                viewAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(view.context,"Retrieve post error : $it", Toast.LENGTH_LONG).show()
        }
        
        editBtn.setOnClickListener { 
            val setupIntent : Intent = Intent(view.context,SetupActivity::class.java)
            startActivity(setupIntent)
        }
        return view
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

            firebaseFirestore.collection("users").document(currentUser.uid).get().addOnSuccessListener {

                if(it != null) {
                    if(it.data?.getValue("desc") != null){
                        desc.text = it.data!!.getValue("desc").toString()
                    }
                }
            }
        }
    }


}
