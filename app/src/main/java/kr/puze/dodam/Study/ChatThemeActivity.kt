package kr.puze.dodam.Study

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_chat_theme.*
import kr.puze.dodam.Adapter.ChatThemRecyclerViewAdapter
import kr.puze.dodam.Item.ChatThemeItem
import kr.puze.dodam.R

class ChatThemeActivity : AppCompatActivity() {

    var items : ArrayList<ChatThemeItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        items.add(ChatThemeItem(R.drawable.ic_logo, "#병원", "일상적인 의사와 환자 간의 대화를 학습합니다."))
        items.add(ChatThemeItem(R.drawable.ic_logo, "#학교", "학교에서 선생님과 학생과의 대화를 학습합니다."))

        val adapter = ChatThemRecyclerViewAdapter(items, this)

        chat_theme_recycler_view.adapter = adapter
        chat_theme_recycler_view.adapter.notifyDataSetChanged()

        adapter.itemClick = object : ChatThemRecyclerViewAdapter.ItemClick {
            override fun onItemClick(view: View?, position: Int) {
                var intent = Intent(this@ChatThemeActivity, ChatActivity::class.java)
                intent.putExtra("theme", items.get(position).title)
                startActivity(intent)
            }
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }
}