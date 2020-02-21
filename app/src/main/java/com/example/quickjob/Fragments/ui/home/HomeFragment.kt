package com.example.quickjob.Fragments.ui.home

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickjob.Activities.HomeActivity
import com.example.quickjob.Adapters.AdViewAdapter
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: AdViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var arr = ArrayList<Advertisement>()
    private var temp = ArrayList<Advertisement>()
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as HomeActivity).showFloatingActionButton()
        firebaseFirestore = FirebaseFirestore.getInstance()
        viewManager = LinearLayoutManager(root.context)
        viewAdapter = AdViewAdapter(root.context, arr)
        viewAdapter.setHasStableIds(true)

        recyclerView = root.findViewById<RecyclerView>(R.id.home_recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        setSearchArr()
        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseFirestore.collection(Constants.POSTS_PATH)
            .addSnapshotListener { snapshots, exception ->

                if (exception != null) {
                    return@addSnapshotListener
                }
                arr.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newItem = dc.document.toObject(Advertisement::class.java)
                        arr.add(newItem)
                        viewAdapter.notifyDataSetChanged()

                    }
                }
            }
    }

    private fun setSearchArr() {

        firebaseFirestore.collection(Constants.POSTS_PATH).get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                for (document in task.result?.documents!!) {
                    val item = document.toObject(Advertisement::class.java)
                    if (item != null) {
                        temp.add(item)
                    }
                }
                viewAdapter.setSearchList(temp)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                viewAdapter.filter.filter(newText)
                return true
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


}