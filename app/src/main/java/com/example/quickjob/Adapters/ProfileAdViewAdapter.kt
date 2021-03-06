package com.example.quickjob.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileAdViewAdapter(private var context: Context, list: ArrayList<Advertisement>) : RecyclerView.Adapter<ProfileAdViewAdapter.MyViewHolder>() {

    private var adList: ArrayList<Advertisement> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.user_ad_row_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = adList[position].title
        var miliseconds: Long = 0
        try {
            miliseconds = adList[position].time?.time!!
        }catch (e: Exception){
            e.printStackTrace()
        }
        val date:String = SimpleDateFormat().format(Date(miliseconds))
        holder.date.text = date
    }

    class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        var title = itemView.findViewById<TextView>(R.id.user_ad_item_title)
        var date = itemView.findViewById<TextView>(R.id.user_ad_item_date)
    }
}