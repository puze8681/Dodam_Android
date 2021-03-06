package kr.puze.dodam.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_study_select.view.*
import kr.puze.dodam.Item.StudySelectData
import kr.puze.dodam.R

class StudySelectGridViewAdapter(private val items: ArrayList<StudySelectData>) : BaseAdapter() {

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
            convertView = inflater.inflate(R.layout.item_study_select, parent, false)
        }
        convertView!!.select_title.text = items[position].title
        convertView.select_sub.text = items[position].sub
        return convertView
    }
}