package kr.puze.dodam.Utils

import butterknife.ButterKnife
import android.support.v7.widget.RecyclerView
import android.view.View


open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        ButterKnife.bind(this, itemView)
    }
}