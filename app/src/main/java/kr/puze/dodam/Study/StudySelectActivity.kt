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
import kotlinx.android.synthetic.main.activity_study_select.*
import kr.puze.dodam.Adapter.StudySelectGridViewAdapter
import kr.puze.dodam.Data.*
import kr.puze.dodam.Item.StudySelectData
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudySelectActivity : AppCompatActivity() {

    var items: ArrayList<StudySelectData> = ArrayList()

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var call: Call<CharData>
        lateinit var ids: ArrayList<String>
        lateinit var syllable_titles: Array<String>
        lateinit var syllable_subs: Array<String>
        lateinit var call_phonetic_list: Call<PhoneticListData>
        lateinit var call_word_list: Call<WordListData>
        lateinit var phonetic_list: List<PhoneticListIndexData>
        lateinit var word_list: List<WordListIndexData>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_select)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        retrofitSetting()

        val intent = intent
        val title = intent.getStringExtra("study")

        context = applicationContext
        prefManager = PrefManager(StudySelectActivity.context)
        ids = ArrayList()

        var resources: Resources = resources
        syllable_titles = resources.getStringArray(R.array.syllable)
        syllable_subs = resources.getStringArray(R.array.syllable_sub)

        when (title) {
            "syllable" -> {
                study_select_title.text = "Alphabet"
                study_select_sub_title.text = "Select the alphabet to learn"
                selectSyllabel()
            }
            "phonogram" -> {
                study_select_title.text = "Phonetic"
                study_select_sub_title.text = "Select the phonetic to learn"
                selectPhonetic()
            }
            "word" -> {
                study_select_title.text = "Word"
                study_select_sub_title.text = "Select the word to learn"
                selectWord()
            }
            else -> finish()
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }
    
    private fun selectSyllabel(){
        if (checkNetwork()) {
            setProgressDialog("알파벳 리스트 로딩 중")
            call = retrofitService.get_char(prefManager.access_token)
            call.enqueue(object : Callback<CharData> {
                override fun onResponse(call: Call<CharData>?, response: Response<CharData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudySelectActivity, "로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            for (i in 0..(user.list.size - 1)) {
                                ids.add(user.list[i].id)
                            }
                            for (i in 0..(syllable_titles.size - 1)) {
                                items.add(StudySelectData(syllable_titles[i], syllable_subs[i], ids[i]))
                                Log.d("SYLLABLE", syllable_titles[i] + " " + syllable_subs[i] + " " + ids[i] + "\n")
                            }
                            if (items.size != 0) {
                                val adapter = StudySelectGridViewAdapter(items)
                                study_select_grid_view.adapter = adapter
                                study_select_grid_view.setOnItemClickListener { adapterView, view, i, l ->
                                    var intent = Intent(this@StudySelectActivity, StudyInActivity::class.java)
                                    intent.putExtra("study", "syllable")
                                    intent.putExtra("syllable", items[i].title)
                                    intent.putExtra("syllable_id", items[i].id)
                                    startActivity(intent)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@StudySelectActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("syllable_code", response.code().toString())
                        Log.e("syllable_err", response.message())
                    }
                }

                override fun onFailure(call: Call<CharData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudySelectActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("syllable_call", t.toString())
                }
            })
        }
    }

    private fun selectPhonetic(){
        if (checkNetwork()) {
            setProgressDialog("표음 리스트 로딩 중")
            call_phonetic_list = retrofitService.get_phonetic(prefManager.access_token)
            call_phonetic_list.enqueue(object : Callback<PhoneticListData> {
                override fun onResponse(call: Call<PhoneticListData>?, response: Response<PhoneticListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudySelectActivity, "표음 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            phonetic_list = user.lectures
                            for(i in 0 .. (phonetic_list.size-1)){
                                items.add(StudySelectData((i+1).toString(),"", phonetic_list[i].id))
                            }
                            
                            if(items.size != 0){
                                val adapter = StudySelectGridViewAdapter(items)
                                study_select_grid_view.adapter = adapter
                                study_select_grid_view.setOnItemClickListener { adapterView, view, i, l ->
                                    var intent = Intent(this@StudySelectActivity, StudyInActivity::class.java)
                                    intent.putExtra("study", "phonogram")
                                    prefManager.phoneticId = i
                                    startActivity(intent)
                                }
                            }
                            
                            Log.d("phonetic_list_data", phonetic_list.toString())
                            Log.d("phonetic_list_data_size", phonetic_list.size.toString())
                        }
                    } else {
                        Toast.makeText(this@StudySelectActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("phonetic_list_code", response.code().toString())
                        Log.e("phonetic_list_err", response.message())
                    }
                }

                override fun onFailure(call: Call<PhoneticListData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudySelectActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("phonetic_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudySelectActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectWord(){
        if (checkNetwork()) {
            setProgressDialog("단어 리스트 로딩 중")
            call_word_list = retrofitService.get_word(prefManager.access_token)
            call_word_list.enqueue(object : Callback<WordListData> {
                override fun onResponse(call: Call<WordListData>?, response: Response<WordListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudySelectActivity, "단어 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            word_list = user.list

                            for(i in 0 .. (word_list.size-1)){
                                items.add(StudySelectData((i+1).toString(),"", word_list[i].id))
                            }

                            if(items.size != 0){
                                val adapter = StudySelectGridViewAdapter(items)
                                study_select_grid_view.adapter = adapter
                                study_select_grid_view.setOnItemClickListener { adapterView, view, i, l ->
                                    var intent = Intent(this@StudySelectActivity, StudyInActivity::class.java)
                                    intent.putExtra("study", "word")
                                    prefManager.wordId = i
                                    startActivity(intent)
                                }
                            }

                            Log.d("word_list_data", word_list.toString())
                            Log.d("word_list_data_size", word_list.size.toString())
                        }
                    } else {
                        Toast.makeText(this@StudySelectActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("word_list_code", response.code().toString())
                        Log.e("word_list_err", response.message())
                    }
                }

                override fun onFailure(call: Call<WordListData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudySelectActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudySelectActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = StudySelectActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

