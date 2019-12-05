package com.example.quickjob.Activities.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.quickjob.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val arr: ArrayList<newMarker> = ArrayList()

        firebaseFirestore.collection("posts").get().addOnSuccessListener {documents ->

            for(document in documents){

                if(document.data["latlng"] != null) {
                    val hashMap = document.data.getValue("latlng") as HashMap<String, Double>
                    val loc: LatLng = LatLng(hashMap["latitude"]!!, hashMap["longitude"]!!)
                    val title: String = document.data.getValue("title").toString()
                    arr.add(newMarker(loc, title))
                }
            }

        }

        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync {
            it.mapType = GoogleMap.MAP_TYPE_NORMAL
            it.clear()

            for(marker in arr){
                it.addMarker(MarkerOptions().position(marker.loc).title(marker.title))
            }
        }

        return root
    }

    private class newMarker(var loc: LatLng, var title: String){

    }


}