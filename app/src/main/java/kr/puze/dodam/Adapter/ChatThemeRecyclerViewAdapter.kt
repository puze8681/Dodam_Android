package kr.puze.dodam.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_chat_theme.view.*
import kr.puze.dodam.Data.DebateThemeData
import kr.puze.dodam.R


class ChatThemeRecyclerViewAdapter(var items: ArrayList<DebateThemeData>, var context: Context) : RecyclerView.Adapter<ChatThemeRecyclerViewAdapter.ViewHolder>() {
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_chat_theme, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position))
        holder.itemView?.setOnClickListener {
            itemClick?.onItemClick(holder.itemView, position)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: DebateThemeData) {
            Glide.with(itemView.context)
                    .load(item.image)
                    .into(itemView.theme_image)
            itemView.theme_title.text = item.red + " vs " + item.blue
            itemView.theme_sub.text = item.deadline
        }
    }

    var itemClick: ItemClick? = null

    interface ItemClick {
        fun onItemClick(view: View?, position: Int)
    }
}