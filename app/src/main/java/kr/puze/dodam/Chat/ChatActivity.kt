package kr.puze.dodam.Chat

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.ChatData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.ActivityChatBinding
import kr.puze.dodam.databinding.ItemChatBinding

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var binding: ActivityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        var intent_ = intent
        var theme = intent_.getStringExtra("theme")

        var datas = ArrayList<ChatData>()
        var data = ChatData("https://lh3.ggpht.com/yVfPv-yLjIuBjpKj41NLkLXmuVv8XzH0m2hf-_sz9lQDv9WB9SX0McB8Jn4bQe4w5Q=s180", "kakao talk", "logo image")
        datas.add(data)

        var chat = LastAdapter(datas, BR.data_chat)
                .map<ChatData, ItemChatBinding>(R.layout.item_chat){

                }
                .into(binding.chatRecyclerView)
    }
}
