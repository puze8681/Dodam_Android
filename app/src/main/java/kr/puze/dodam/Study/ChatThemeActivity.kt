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
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_chat_theme.*
import kr.puze.dodam.Adapter.DebateThemeRecyclerViewAdapter
import kr.puze.dodam.Data.DebateThemeData
import kr.puze.dodam.Data.DebateThemeListData
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatThemeActivity : AppCompatActivity() {

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var call: Call<DebateThemeListData>
        lateinit var token: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()
        prefManager = PrefManager(this@ChatThemeActivity)

        token = prefManager.access_token

        actionbar_back.setOnClickListener {
            finish()
        }

        retrofitSetting()
        getThemeList()
    }

    private fun getThemeList(){
        if (checkNetwork()) {
            setProgressDialog("채팅 테마 리스트 로딩 중")

            val items : ArrayList<DebateThemeData> = ArrayList()
            call = retrofitService.get_debate_theme(token)
            call.enqueue(object : Callback<DebateThemeListData> {
                override fun onResponse(call: Call<DebateThemeListData>?, response: Response<DebateThemeListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val data = response.body()
                        if (data != null) {
                            Toast.makeText(this@ChatThemeActivity, "단어 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            for (i in data.list){
                                items.add(i)
                            }
                            Log.d("word_list_data", data.list.toString())
                            setRecyclerView(items)
                        }
                    } else {
                        Toast.makeText(this@ChatThemeActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("word_list_code", response.code().toString())
                        Log.e("word_list_err", response.message())
                    }
                }

                override fun onFailure(call: Call<DebateThemeListData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@ChatThemeActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@ChatThemeActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun setRecyclerView(items: ArrayList<DebateThemeData>){
        val adapter = DebateThemeRecyclerViewAdapter(items, this)

        chat_theme_recycler_view.adapter = adapter
        chat_theme_recycler_view.adapter.notifyDataSetChanged()

        adapter.itemClick = object : DebateThemeRecyclerViewAdapter.ItemClick {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(this@ChatThemeActivity, ChatActivity::class.java)
                intent.putExtra("theme_id", items[position].id)
                intent.putExtra("theme_title", items[position].red + " vs " + items[position].blue)
                intent.putExtra("token", token)
                startActivity(intent)
            }
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = this@ChatThemeActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
}
