package com.example.quickjob.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quickjob.Activities.AdDetailActivity
import com.example.quickjob.Classes.Advertisement
import com.example.quickjob.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdViewAdapter(private var context: Context, list: ArrayList<Advertisement>) :
    RecyclerView.Adapter<AdViewAdapter.MyViewHolder>(), Filterable{

    private var adList: ArrayList<Advertisement> = list
    private var searchList: List<Advertisement>
    private var options : RequestOptions


    init {
        options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.loading_shape)
        searchList = ArrayList(list)
    }

    fun setSearchList(arr: ArrayList<Advertisement>){
        searchList = ArrayList(arr)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.image.animation = AnimationUtils.loadAnimation(context,R.anim.fade_transtition_animation)
        viewHolder.animContainer.animation = AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation)
        viewHolder.title.text = adList[position].title
        viewHolder.payment.text = adList[position].payment

        Glide.with(context).load(adList[position].img).apply(options).into(viewHolder.image)
        viewHolder.distance.text = adList[position].location

        var miliseconds: Long = 0
        try {
            miliseconds = adList[position].time?.time!!
        }catch (e: Exception){
            e.printStackTrace()
        }
        val dateText:String = SimpleDateFormat().format(Date(miliseconds))
        viewHolder.date.text = dateText

        viewHolder.mainContainer.setOnClickListener {

            val detailIntent: Intent = Intent(context,AdDetailActivity::class.java)
            detailIntent.putExtra("title", adList[position].title)
            detailIntent.putExtra("desc",adList[position].desc)
            detailIntent.putExtra("timestamp",dateText)
            detailIntent.putExtra("category",adList[position].category)
            detailIntent.putExtra("payment",adList[position].payment)
            detailIntent.putExtra("img",adList[position].img)
            detailIntent.putExtra("user",adList[position].user)
            detailIntent.putExtra("location",adList[position].location)

            context.startActivity(detailIntent)

        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

   override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.advertisement_row_item,p0,false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {

      return  adList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        var title: TextView = itemView.findViewById(R.id.ad_item_title)
        var date: TextView = itemView.findViewById(R.id.ad_item_date)
        var distance: TextView = itemView.findViewById(R.id.ad_item_distance)
        var payment: TextView = itemView.findViewById(R.id.ad_item_payment)
        var image: ImageView = itemView.findViewById(R.id.ad_item_thumbnail)
        val mainContainer: ConstraintLayout = itemView.findViewById(R.id.item_container)
        val animContainer : ConstraintLayout = itemView.findViewById(R.id.item_container2)

        override fun onClick(p0: View?) {

        }

    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter = object : Filter() {
        override fun performFiltering(constraints: CharSequence?): FilterResults {

            val filteredList = ArrayList<Advertisement>()
            if(constraints == null || constraints.isEmpty() || constraints == ""){
                filteredList.addAll(searchList)
            }else {
                val filterPattern = constraints.toString().toLowerCase().trim()

                for(item in searchList){
                    if(item.title.toLowerCase().contains(filterPattern)){
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraints: CharSequence?, results: FilterResults?) {

            adList.clear()
            adList.addAll(results?.values as List<Advertisement>)
            notifyDataSetChanged()
        }

    }
}