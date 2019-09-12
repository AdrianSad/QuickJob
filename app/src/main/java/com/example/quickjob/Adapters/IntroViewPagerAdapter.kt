package com.example.quickjob.Adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.quickjob.Classes.ScreenItem
import com.example.quickjob.R

class IntroViewPagerAdapter(var mContext: Context, var mList: List<ScreenItem>) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layoutScreen: View = inflater.inflate(R.layout.layout_screen,null)

        val imgSlide: ImageView = layoutScreen.findViewById(R.id.intro_img)
        val title:TextView = layoutScreen.findViewById(R.id.intro_title)
        val desc: TextView = layoutScreen.findViewById(R.id.intro_description)

        title.text = mList[position].title
        desc.text = mList[position].desc
        imgSlide.setImageResource(mList[position].img)

        container.addView(layoutScreen)

        return layoutScreen
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {

        return p0 == p1
    }

    override fun getCount(): Int {

       return mList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {

        container.removeView(obj as View)
    }
}