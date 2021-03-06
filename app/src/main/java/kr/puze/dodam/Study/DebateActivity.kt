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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.actionbar_orange_debate.*
import kotlinx.android.synthetic.main.activity_debate.*
import kotlinx.android.synthetic.main.item_debate_input.*
import kr.puze.dodam.Adapter.ChatMessageAdapter
import kr.puze.dodam.Data.ChatMessage
import kr.puze.dodam.Data.RoomData
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.Constants
import kr.puze.dodam.Utils.PrefManager
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException

class DebateActivity : AppCompatActivity() {

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
        lateinit var account_id: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#ff5722")
        supportActionBar!!.hide()

        actionbar_back_white.setOnClickListener {
            finish()
        }

        prefManager = PrefManager(this@DebateActivity)
        chat_intent = intent
        token = prefManager.access_token
        userName = prefManager.userName
        account_id = prefManager.account_id

        constants = Constants()
        val theme_id = intent.getStringExtra("theme_id")
        Log.d("theme_id", intent.getStringExtra("theme_id"))
        val theme_title = intent.getStringExtra("theme_title")
        val theme_team = intent.getStringExtra("theme_team")
        debate_title.text = "THEME : $theme_title"

        initializeView()
        setupRecyclerView()

        getThemeRoom(theme_id, theme_team)
        actionbar_back_white.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initializeView() {
        debate_exit.isEnabled = false
        debate_send.isEnabled = false
        debate_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                debate_send.isEnabled = charSequence.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun setupRecyclerView() {
        mAdapter = ChatMessageAdapter()
        mAdapter.ChatMessageAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        debate_recycler_view.adapter = mAdapter
        debate_recycler_view.setHasFixedSize(true)
        debate_recycler_view.layoutManager = layoutManager

        debate_recycler_view.addOnLayoutChangeListener { view: View, newLeft: Int, newTop: Int, newRight: Int, newBottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int ->
            if (newBottom < oldBottom) {
                debate_recycler_view.postDelayed({
                    kotlin.run {
                        debate_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
                    }
                }, 100)
            }
        }
    }

    private fun setupSocketClient() {
        try {
            Log.d("socket_setup", room_id)
            mSocket = IO.socket(constants.SOCKET_URL)
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
//            mSocket.on(constants.EVENT_SYSTEM, onMessageReceived)
//            mSocket.on(constants.EVENT_MESSAGE, onMessageReceived)

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
            debate_exit.isEnabled = true
            Log.d("socket_onconnect", room_id)
            Log.d("socket_onconnect_exit", debate_exit.isEnabled.toString())
            sendData.put("room", room_id)
            sendData.put("user", account_id)
            mSocket.emit(constants.EVENT_ENTERED_ROOM, sendData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * Socket 메시지 받는 Listener
     */
    private var onMessageReceived: Emitter.Listener = Emitter.Listener {
        val rcvData = JSONObject()

        try {
            val messageType: String = rcvData.optString("type")
            val messageContent: String = rcvData.optString("message")
//            val messageOwnerID: String = rcvData.optJSONObject("sender").optString("id")
            val messageOwnerJson: JSONObject = rcvData.optJSONObject("sender")
            val messageOwnerName: String = messageOwnerJson.optString("name")
//            val messageOwnerProfile: String = rcvData.optJSONObject("sender").optString("profile")
            var type: String = {
                when(messageType){
                    "connect" -> constants.MESSAGE_TYPE_SYSTEM
                    "normal" -> constants.MESSAGE_TYPE_RECEIVE
                    else -> {
                    }
                }
            }.toString()
            val message = ChatMessage(messageType, type, messageOwnerName, messageContent)
            Log.d("socket_receive_", message.toString())

            runOnUiThread {
                mAdapter.addItems(message)
                debate_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
            }
        }catch (e: NullPointerException){
            Log.e("socket_receive", "onMessageReceived", e)
        }

    }

    /**
     * Socket 메시지 보내는 Method
     */
    private fun sendButtonTouchUp() {
        val sendMessage: String = debate_edit.text.toString()
        Log.d("message", "message: $sendMessage")

        // 서버로 전송할 데이터 생성 및 메시지 이벤트 보냄.
        val sendData = JSONObject()
        try {
            Log.d("socket_send", "")
            sendData.put(constants.SEND_DATA_MESSAGE, sendMessage)
            sendData.put(constants.SEND_DATA_USERNAME, userName)
            sendData.put(constants.EVENT_ENTERED_ROOM, room_id)
            mSocket.emit(constants.EVENT_MESSAGE, sendData)
            Log.d("socket_send", sendData.toString())
            // 전송 후, EditText 초기화
            debate_edit.text = null

            // 본인의 메시지는 서버에서 전달받지 않고, 바로 생성한다.
            var message = ChatMessage("", constants.MESSAGE_TYPE_SELF, userName, sendMessage)
            mAdapter.addItems(message)
            debate_recycler_view.smoothScrollToPosition(mAdapter.itemCount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun exitButtonTouchUp() {
        Log.d("debate_exit", "onClick")
        if (checkNetwork()) {
            setProgressDialog("채팅 방 종료 중")

            call = retrofitService.get_room_quit(token, room_id)
            call.enqueue(object : Callback<RoomData> {
                override fun onResponse(call: Call<RoomData>?, response: Response<RoomData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        Toast.makeText(this@DebateActivity, "채팅 방 나가기 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@DebateActivity, "채팅 방 나가기 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RoomData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@DebateActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            finish()
            Toast.makeText(this@DebateActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
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
                            Toast.makeText(this@DebateActivity, "채팅 방 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            Log.d("chat_room_id", data.room_id)
                            room_id = data.room_id
                            if (!room_id.isNullOrEmpty()) {
                                setupSocketClient()
                                debate_send.setOnClickListener {
                                    sendButtonTouchUp()
                                }
                                debate_exit.setOnClickListener {
                                    exitButtonTouchUp()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@DebateActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("chat_code", response.code().toString())
                        Log.e("chat_err", response.message())
                    }
                }

                override fun onFailure(call: Call<RoomData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@DebateActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@DebateActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = this@DebateActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
