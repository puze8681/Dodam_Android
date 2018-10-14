package kr.puze.dodam.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import com.squareup.picasso.Picasso
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import kotlinx.android.synthetic.main.item_syllable.view.*
import kr.puze.dodam.Item.SyllableSelectItem
import kr.puze.dodam.R

class SyllableSelectGridViewAdapter(private val items: ArrayList<SyllableSelectItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val context = parent.context

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_syllable, parent, false)
        }
        convertView!!.syllable_title.text = items[position].title
        convertView.syllable_sub.text = items[position].sub
        return convertView
    }
}