package com.example.quickjob.Fragments.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quickjob.Activities.HomeActivity
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapFragment : Fragment() {

    private val arr: ArrayList<NewMarker> = ArrayList()
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        (activity as HomeActivity).hideFloatingActionButton()

        initializeMap()
        return root
    }

    private fun initializeMap(){
        firebaseFirestore.collection(Constants.POSTS_PATH).get().addOnSuccessListener { documents ->
            for(document in documents){
                if(document.data[Constants.LATITUDE_LONGITUDE] != null) {
                    val locationMap = document.data.getValue(Constants.LATITUDE_LONGITUDE) as HashMap<*, *>
                    val loc: LatLng = LatLng((locationMap["latitude"] as Double), (locationMap["longitude"] as Double))
                    val title: String = document.data.getValue(Constants.TITLE).toString()
                    arr.add(NewMarker(loc, title))
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
        }
    }

    private class NewMarker(var loc: LatLng, var title: String){}


}