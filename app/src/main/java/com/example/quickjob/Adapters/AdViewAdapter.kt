package com.example.quickjob.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
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
import com.example.quickjob.ConstantValues.Constants
import com.example.quickjob.R
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdViewAdapter(private var context: Context, list: ArrayList<Advertisement>) :
    RecyclerView.Adapter<AdViewAdapter.MyViewHolder>(), Filterable {

    private var adList: ArrayList<Advertisement> = list
    private var searchList: List<Advertisement>
    private var options: RequestOptions


    init {
        options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.loading_shape)
        searchList = ArrayList(list)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.image.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transtition_animation)
        viewHolder.animContainer.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation)
        viewHolder.title.text = adList[position].title
        viewHolder.payment.text = adList[position].payment

        if ((viewHolder.payment.text as String).toIntOrNull() == null) {
            viewHolder.payment.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_cake_black_16dp,
                0,
                0,
                0
            )
            viewHolder.payment.setTextColor(Color.parseColor(Constants.PAYMENT_RED_COLOR))
        } else {
            viewHolder.payment.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_attach_money_black_24dp,
                0,
                0,
                0
            )
            viewHolder.payment.setTextColor(Color.parseColor(Constants.PAYMENT_GREEN_COLOR))
        }
        Glide.with(context).load(adList[position].img).apply(options).into(viewHolder.image)
        viewHolder.distance.text = adList[position].location
        val dateText: String = convertToDate(adList[position].time?.time!!)
        viewHolder.date.text = dateText

        viewHolder.mainContainer.setOnClickListener {
            putData(position, dateText)
        }

    }

    private fun putData(position: Int, dateText: String) {
        val detailIntent: Intent = Intent(context, AdDetailActivity::class.java)
        detailIntent.putExtra(Constants.TITLE, adList[position].title)
        detailIntent.putExtra(Constants.DESCRIPTION, adList[position].desc)
        detailIntent.putExtra(Constants.TIMESTAMP, dateText)
        detailIntent.putExtra(Constants.CATEGORY, adList[position].category)
        detailIntent.putExtra(Constants.PAYMENT, adList[position].payment)
        detailIntent.putExtra(Constants.IMAGE, adList[position].img)
        detailIntent.putExtra(Constants.USER_ID, adList[position].user)
        detailIntent.putExtra(Constants.LOCATION, adList[position].location)
        detailIntent.putExtra(Constants.USER_ID, adList[position].user)
        detailIntent.putExtra(Constants.POST_ID, adList[position].postID)
        detailIntent.putExtra(Constants.USER_POST_ID, adList[position].userPostID)
        detailIntent.putExtra(Constants.IMAGE_NAME, adList[position].img_name)
        context.startActivity(detailIntent)
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
        val view = layoutInflater.inflate(R.layout.advertisement_row_item, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var title: TextView = itemView.findViewById(R.id.ad_item_title)
        var date: TextView = itemView.findViewById(R.id.ad_item_date)
        var distance: TextView = itemView.findViewById(R.id.ad_item_distance)
        var payment: TextView = itemView.findViewById(R.id.ad_item_payment)
        var image: ImageView = itemView.findViewById(R.id.ad_item_thumbnail)
        val mainContainer: ConstraintLayout = itemView.findViewById(R.id.item_container)
        val animContainer: ConstraintLayout = itemView.findViewById(R.id.item_container2)

        override fun onClick(p0: View?) {
        }
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter = object : Filter() {
        override fun performFiltering(constraints: CharSequence?): FilterResults {

            val filteredList = ArrayList<Advertisement>()
            if (constraints == null || constraints.isEmpty() || constraints == "") {
                filteredList.addAll(searchList)
            } else {
                val filterPattern = constraints.toString().toLowerCase().trim()

                for (item in searchList) {
                    if (item.title.toLowerCase().contains(filterPattern) || item.location.toLowerCase().contains(
                            filterPattern
                        )
                    ) {
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

    fun setSearchList(arr: ArrayList<Advertisement>) {
        searchList = ArrayList(arr)
    }

    private fun convertToDate(ms: Long): String {

        var miliseconds: Long = 0
        try {
            miliseconds = ms
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return SimpleDateFormat().format(Date(miliseconds))
    }


}