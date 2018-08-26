package kr.puze.dodam.Chat

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.ChatThemeData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.ActivityChatThemeBinding
import kr.puze.dodam.databinding.ItemChatThemeBinding

class ChatThemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_theme)
        supportActionBar!!.title = "ChatThemeActivity"

        val binding: ActivityChatThemeBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_theme)

        var datas = ArrayList<ChatThemeData>()
        var data = ChatThemeData("https://lh3.ggpht.com/yVfPv-yLjIuBjpKj41NLkLXmuVv8XzH0m2hf-_sz9lQDv9WB9SX0McB8Jn4bQe4w5Q=s180", "kakao talk", "logo image")
        datas.add(data)

        var chat = LastAdapter(datas, BR.data_chat_theme)
                .map<ChatThemeData, ItemChatThemeBinding>(R.layout.item_chat_theme){
                    onClick {
                        var item =it.binding.dataChatTheme
                        startActivity(Intent(this@ChatThemeActivity, ChatActivity::class.java).putExtra("theme", item!!.title))
                    }
                }
                .into(binding.chatThemeRecyclerView)
    }
}
