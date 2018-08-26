package kr.puze.dodam.Chat

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.ChatData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.title = "ChatActivity"

        var binding: ActivityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        var intent_ = intent
        var theme = intent_.getStringExtra("theme")

        var datas = ArrayList<ChatData>()
        var data = ChatData("bot", "dodam dodam", "2018.08.26")
        datas.add(data)

        var chat = LastAdapter(datas, BR.data_chat)
                .layout { item, position ->
                    when(item){
                        is ChatData -> if (datas.get(position).who.equals("user")) R.layout.item_chat_user else R.layout.item_chat_bot
                        else -> R.layout.item_chat_user
                    }
                }
                .into(binding.chatRecyclerView)
    }
}
