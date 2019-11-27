package com.example.quickjob.Activities.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickjob.Adapters.AdViewAdapter
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class HomeFragment : Fragment() {

   // private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private  var arr = arrayListOf<Advertisement>()
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       /* homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)*/
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
       /* homeViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        firebaseFirestore = FirebaseFirestore.getInstance()

        viewManager = LinearLayoutManager(root.context)
        viewAdapter = AdViewAdapter(root.context,arr)

        recyclerView = root.findViewById<RecyclerView>(R.id.home_recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        firebaseFirestore.collection("posts").get().addOnSuccessListener { documents ->

            for(document in documents){

                val item = document.toObject(Advertisement::class.java)
                arr.add(item)
                viewAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(root.context,"Retrieve post error : $it", Toast.LENGTH_LONG).show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseFirestore.collection("posts").addSnapshotListener{snapshots, exception ->

            if(exception != null){
                return@addSnapshotListener
            }

            for(dc in snapshots!!.documentChanges){
                if(dc.type == DocumentChange.Type.ADDED){
                    val newItem = dc.document.toObject(Advertisement::class.java)
                    arr.add(newItem)
                    viewAdapter.notifyDataSetChanged()
                }
            }
        }
    }


}