package kr.puze.dodam.Study

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_chat.*
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException
import kr.puze.dodam.Adapter.ChatMessageAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kr.puze.dodam.Data.ChatMessage
import kr.puze.dodam.Utils.Constants
import org.json.JSONException
import org.json.JSONObject
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kr.puze.dodam.Data.RoomData

class ChatActivity : AppCompatActivity() {

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var call: Call<RoomData>

        lateinit var mSocket: Socket
        lateinit var userName: String
        @SuppressLint("StaticFieldLeak")
        lateinit var mAdapter: ChatMessageAdapter
        lateinit var constants: Constants
        lateinit var room_id: String

        lateinit var chat_intent: Intent
        lateinit var token: String
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        chat_intent = intent
        prefManager = PrefManager(this@ChatActivity)
        userName = prefManager.userName
        token = prefManager.access_token

        constants = Constants()
        val theme_id = intent.getStringExtra("theme_id")
        val theme_title = intent.getStringExtra("theme_title")
        chat_title.text = "THEME : $theme_title"

        getThemeRoom(theme_id, "red")
        actionbar_back.setOnClickListener {
            finish()
        }

        initializeView()
        setupRecyclerView()
        setupSocketClient()

        chat_send_button.setOnClickListener {
            sendButtonTouchUp()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initializeView() {
        chat_send_button.isEnabled = false
        chat_send_button.setTextColor(R.color.textDisabled)

        chat_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotEmpty()) {
                    chat_send_button.isEnabled = true
                    chat_send_button.setTextColor(R.color.textEnabled)
                } else {
                    chat_send_button.isEnabled = false
                    chat_send_button.setTextColor(R.color.textDisabled)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun setupRecyclerView() {
        mAdapter = ChatMessageAdapter()
        mAdapter.ChatMessageAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        chat_recycler_view.setHasFixedSize(true)
        chat_recycler_view.layoutManager = layoutManager
        chat_recycler_view.adapter = mAdapter

        chat_recycler_view.addOnLayoutChangeListener { view: View, newLeft: Int, newTop: Int, newRight: Int, newBottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int ->
            if (newBottom < oldBottom) {
                chat_recycler_view.postDelayed({
                    kotlin.run {
                        chat_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
                    }
                }, 100)
            }
        }
    }

    private fun setupSocketClient() {
        try {
            mSocket = IO.socket(constants.SOCKET_URL)
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on(constants.EVENT_SYSTEM, onMessageReceived)
            mSocket.on(constants.EVENT_MESSAGE, onMessageReceived)

            mSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

    }

    /**
     * Socket Server 연결 Listener
     */
    private var onConnect: Emitter.Listener = Emitter.Listener {
        val sendData = JSONObject()
        try {
            sendData.put("room", room_id)
            sendData.put("user", token)
            mSocket.emit(constants.EVENT_ENTERED_ROOM, sendData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * Socket 메시지 받는 Listener
     */
    private var onMessageReceived: Emitter.Listener = Emitter.Listener {
//        val rcvData = JSONObject()
//        val userAction: String = rcvData.optString("action")
//        val messageType: String = rcvData.optString("type")
//        val messageContent: String = rcvData.optString("message")
//        val messageOwnerID: String = rcvData.optJSONObject("sender").optString("id")
//        val messageOwnerName: String = rcvData.optJSONObject("sender").optString("name")
//        val messageOwnerProfile: String = rcvData.optJSONObject("sender").optString("profile")
//
//        val message = ChatMessage(userAction, constants.MESSAGE_TYPE_RECEIVE, messageOwnerName, messageContent)
//        Log.d("socket_receive", message.toString())
//
//        runOnUiThread {
//            mAdapter.addItems(message)
//            chat_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
//        }
    }

    /**
     * Socket 메시지 보내는 Method
     */
    private fun sendButtonTouchUp() {
        val sendMessage: String = chat_edit_text.text.toString()
        Log.d("message", "message: $sendMessage")

        // 서버로 전송할 데이터 생성 및 메시지 이벤트 보냄.
        val sendData = JSONObject()
        try {
            sendData.put(constants.SEND_DATA_MESSAGE, sendMessage)
            sendData.put(constants.SEND_DATA_USERNAME, userName)
            sendData.put(constants.EVENT_ENTERED_ROOM, room_id)
            mSocket.emit(constants.EVENT_MESSAGE, sendData)

            // 전송 후, EditText 초기화
            chat_edit_text.text = null

            // 본인의 메시지는 서버에서 전달받지 않고, 바로 생성한다.
            var message = ChatMessage("", constants.MESSAGE_TYPE_SELF, userName, sendMessage)
            mAdapter.addItems(message)
            chat_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * Socket room_id 값 받아오는 Method
     */
    private fun getThemeRoom(theme_id: String, team: String) {
        retrofitSetting()
        if (checkNetwork()) {
            setProgressDialog("채팅 방 로딩 중")

            call = retrofitService.post_debate_theme_join(token, theme_id, team)
            call.enqueue(object : Callback<RoomData> {
                override fun onResponse(call: Call<RoomData>?, response: Response<RoomData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val data = response.body()
                        if (data != null) {
                            Toast.makeText(this@ChatActivity, "채팅 방 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            Log.d("chat_room_id", data.theme_id)
                            room_id = data.theme_id
                        }
                    } else {
                        Toast.makeText(this@ChatActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("chat_code", response.code().toString())
                        Log.e("chat_err", response.message())
                    }
                }

                override fun onFailure(call: Call<RoomData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@ChatActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@ChatActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.isConnectedOrConnecting
    }

    private fun retrofitSetting() {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://api.dodam.io")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }

    public override fun onDestroy() {
        super.onDestroy()

        mSocket.disconnect()
        mSocket.off("new message connect", onConnect)
        mSocket.off("new message messageReceived", onMessageReceived)
    }
}