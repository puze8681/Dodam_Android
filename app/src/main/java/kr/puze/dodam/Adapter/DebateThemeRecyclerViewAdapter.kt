package kr.puze.dodam.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_debate_theme.view.*
import kr.puze.dodam.Data.DebateThemeData
import kr.puze.dodam.R
import kr.puze.dodam.Study.DebateActivity
import kr.puze.dodam.Study.DebateThemeActivity


class DebateThemeRecyclerViewAdapter(var items: ArrayList<DebateThemeData>, var context: Context) : RecyclerView.Adapter<DebateThemeRecyclerViewAdapter.ViewHolder>() {
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_debate_theme, null)
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
        @SuppressLint("SetTextI18n")
        fun bind(item: DebateThemeData) {
            itemView.theme_title.text = item.red + " vs " + item.blue
            itemView.debate_theme_red.text = item.red
            itemView.debate_theme_blue.text = item.blue

            itemView.debate_theme_red.setOnClickListener {
                val intent = Intent(itemView.context, DebateActivity::class.java)
                intent.putExtra("theme_id", item.id)
                intent.putExtra("theme_title", item.red + " vs " + item.blue)
                intent.putExtra("theme_team", "red")
                intent.putExtra("token", DebateThemeActivity.token)
                itemView.context.startActivity(intent)
            }

            itemView.debate_theme_blue.setOnClickListener {
                val adapter_intent = Intent(itemView.context, DebateActivity::class.java)
                adapter_intent.putExtra("theme_id", item.id)
                adapter_intent.putExtra("theme_title", item.red + " vs " + item.blue)
                adapter_intent.putExtra("theme_team", "blue")
                adapter_intent.putExtra("token", DebateThemeActivity.token)
                itemView.context.startActivity(adapter_intent)
            }
        }
    }

    var itemClick: ItemClick? = null

    interface ItemClick {
        fun onItemClick(view: View?, position: Int)
    }
}