package com.example.quickjob.Adapters

import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.location.Location
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.R
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import org.w3c.dom.Text
import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdViewAdapter(private var context: Context, list: ArrayList<Advertisement>) :
    RecyclerView.Adapter<AdViewAdapter.MyViewHolder>() {

    private var adList: ArrayList<Advertisement> = list
    private var options : RequestOptions
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    init {
        options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.loading_shape)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.image.animation = AnimationUtils.loadAnimation(context,R.anim.fade_transtition_animation)
        viewHolder.container1.animation = AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation)
        viewHolder.container2.animation = AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation)
        viewHolder.title.text = adList[position].title
        viewHolder.category.text = adList[position].category
        viewHolder.payment.text = adList[position].payment

        Glide.with(context).load(adList[position].img).apply(options).into(viewHolder.image)
        viewHolder.distance.text = adList[position].location

        var miliseconds: Long = 0
        try {
            miliseconds = adList[position].time?.time!!
        }catch (e: Exception){
            e.printStackTrace()
        }
        val sdf:String = SimpleDateFormat().format(Date(miliseconds))
        viewHolder.date.text = sdf

        //fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                //SphericalUtil.computeDistanceBetween(adList[position].location, LatLng(location.latitude,location.longitude)).toString()

        //}

        viewHolder.mainContainer.setOnClickListener {

            var miliseconds: Long = 0
            try {
                miliseconds = adList[position].time?.time!!
            }catch (e: Exception){
                e.printStackTrace()
            }

            val detailIntent: Intent = Intent(context,AdDetailActivity::class.java)
            detailIntent.putExtra("title", adList[position].title)
            detailIntent.putExtra("desc",adList[position].desc)
            detailIntent.putExtra("timestamp",miliseconds)
            detailIntent.putExtra("category",adList[position].category)
            detailIntent.putExtra("payment",adList[position].payment)
            detailIntent.putExtra("img",adList[position].img)
            detailIntent.putExtra("user",adList[position].user)
            detailIntent.putExtra("location",adList[position].location)

            context.startActivity(detailIntent)

        }

    }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.advertisement_row_item,p0,false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {

      return  adList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        var title: TextView = itemView.findViewById(R.id.ad_item_title)
        var category: TextView = itemView.findViewById(R.id.ad_item_category)
        var date: TextView = itemView.findViewById(R.id.ad_item_date)
        var distance: TextView = itemView.findViewById(R.id.ad_item_distance)
        var payment: TextView = itemView.findViewById(R.id.ad_item_payment)
        var image: ImageView = itemView.findViewById(R.id.ad_item_thumbnail)
        var container1: LinearLayout = itemView.findViewById(R.id.item_linearlayout1)
        var container2: LinearLayout = itemView.findViewById(R.id.item_linearlayout2)
        val mainContainer: LinearLayout = itemView.findViewById(R.id.item_container)

        override fun onClick(p0: View?) {

        }

    }
}