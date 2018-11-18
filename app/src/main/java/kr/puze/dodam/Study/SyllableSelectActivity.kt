package kr.puze.dodam.Study

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_syllable_select.*
import kr.puze.dodam.Adapter.SyllableSelectGridViewAdapter
import kr.puze.dodam.Data.CharData
import kr.puze.dodam.Item.SyllableSelectItem
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyllableSelectActivity : AppCompatActivity() {

    var items: ArrayList<SyllableSelectItem> = ArrayList()

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var call: Call<CharData>
        lateinit var ids: ArrayList<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_syllable_select)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        retrofitSetting()
        context = applicationContext
        prefManager = PrefManager(SyllableSelectActivity.context)
        ids = ArrayList()

        var resources: Resources = resources
        var titles: Array<String> = resources.getStringArray(R.array.syllable)
        var subs: Array<String> = resources.getStringArray(R.array.syllable_sub)

        setProgressDialog("로딩 중")

        if (checkNetwork()) {
            call = retrofitService.get_char(prefManager.access_token)
            call.enqueue(object : Callback<CharData> {
                override fun onResponse(call: Call<CharData>?, response: Response<CharData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@SyllableSelectActivity, "로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            for (i in 0..(user.list.size - 1)) {
                                ids.add(user.list[i].id)
                            }
                            for (i in 0..(titles.size - 1)) {
                                items.add(SyllableSelectItem(titles[i], subs[i], ids[i]))
                                Log.d("SYLLABLE", titles[i] + " " + subs[i] + " " + ids[i] + "\n")
                            }
                            if (items.size != 0) {
                                val adapter = SyllableSelectGridViewAdapter(items)
                                syllable_select_grid_view.adapter = adapter
                                syllable_select_grid_view.setOnItemClickListener { adapterView, view, i, l ->
                                    var intent = Intent(this@SyllableSelectActivity, StudyInActivity::class.java)
                                    intent.putExtra("study", "syllable")
                                    intent.putExtra("syllable", items[i].title)
                                    intent.putExtra("syllable_id", items[i].id)
                                    startActivity(intent)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@SyllableSelectActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("syllable_code", response.code().toString())
                        Log.e("syllable_err", response.message())
                    }
                }

                override fun onFailure(call: Call<CharData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@SyllableSelectActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("syllable_call", t.toString())
                }
            })
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = SyllableSelectActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

