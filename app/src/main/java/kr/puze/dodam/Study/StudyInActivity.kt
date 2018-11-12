package kr.puze.dodam.Study

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_study_in.*
import kotlinx.android.synthetic.main.item_study_bottom.*
import kr.puze.dodam.Data.CharData
import kr.puze.dodam.Data.WordData
import kr.puze.dodam.Data.WordListData
import kr.puze.dodam.Data.WordListIndexData
import kr.puze.dodam.R
import kr.puze.dodam.R.id.study_problem_one
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class StudyInActivity : AppCompatActivity() {

    var word_index: Int = 0

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var call_char: Call<CharData>
        lateinit var call_word: Call<WordData>
        lateinit var call_word_list: Call<WordListData>
        lateinit var tts: TextToSpeech
        lateinit var word_list: List<WordListIndexData>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_in)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        val intent = getIntent()
        val title = intent.getStringExtra("study")

        context = applicationContext
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.KOREAN
                tts.setSpeechRate(0.5f)
            }
        })

        prefManager = PrefManager(this@StudyInActivity)
        retrofitSetting()

        when (title) {
            "syllable" -> {
                study_title.text = "Alphabet"
                study_sub.text = "Learn Korean with pictures"
                callSyllable()
            }
            "phonogram" -> {
                study_title.text = "Phonetic"
                study_sub.text = "Choose the right pronunciation"
                callPhonetic()
            }
            "word" -> {
                study_title.text = "Word"
                study_sub.text = "Choose the right word to fill in the blank"
                word_index = prefManager.wordId
                callWordList()
            }
            else -> finish()
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }

    private fun callSyllable() {
        if (checkNetwork()) {
            setProgressDialog("자모음 로딩 중")
            study_text.visibility = View.GONE
            study_problem_one.text = intent.getStringExtra("syllable")
            study_problem_two.visibility = View.GONE
            study_problem_three.visibility = View.GONE
            study_problem_four.visibility = View.GONE
            study_next.visibility = View.GONE
            study_back.visibility = View.GONE

            study_problem_one.setOnClickListener {
                tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }
            val syllableId = intent.getStringExtra("syllable_id")
            call_char = retrofitService.get_char_id(syllableId)
            call_char.enqueue(object : Callback<CharData> {
                override fun onResponse(call: Call<CharData>?, response: Response<CharData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudyInActivity, "로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            //나중에 서버연동
//                            Glide.with(context)
//                                    .load(user.list[0].image)
//                                    .into(study_image)
                            Glide.with(context)
                                    .load("http://placid-pancake.surge.sh/images/glove.jpg")
                                    .into(study_image)
                            Log.d("syllable_data", user.list[0].toString())
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("syllable_code", response.code().toString())
                        Log.e("syllable_err", response.message())
                    }
                }

                override fun onFailure(call: Call<CharData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("syllable_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun callPhonetic() {
        study_image.visibility = View.GONE
        study_text.text = "Hotel"
        study_problem_one.text = "자델"
        study_problem_two.text = "호델"
        study_problem_three.text = "후델"
        study_problem_four.text = "호텔"
        study_next.visibility = View.GONE
        study_back.visibility = View.GONE

        study_problem_one.setOnClickListener {
            tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
        study_problem_two.setOnClickListener {
            tts.speak(study_problem_two.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
        study_problem_three.setOnClickListener {
            tts.speak(study_problem_three.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
        study_problem_four.setOnClickListener {
            tts.speak(study_problem_four.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun callWord(index: Int) {
        if (checkNetwork()) {
            setProgressDialog("단어 로딩 중")
            study_text.visibility = View.GONE
            study_problem_two.visibility = View.GONE
            study_problem_three.visibility = View.GONE
            study_problem_four.visibility = View.GONE

            study_problem_one.setOnClickListener {
                tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }
            call_word = retrofitService.get_word_id(word_list[index].id)
            call_word.enqueue(object : Callback<WordData> {
                override fun onResponse(call: Call<WordData>?, response: Response<WordData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val word = response.body()
                        if (word != null) {
                            Toast.makeText(this@StudyInActivity, "단어 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            //이것도 나중에 서버연동 제대로
//                            Glide.with(context)
//                                    .load(word.image)
//                                    .into(study_image)
                            Glide.with(context)
                                    .load("http://undesirable-growth.surge.sh/images/water.jpg")
                                    .into(study_image)
                            study_problem_one.run {
                                //text = word.word
                                text = "물"
                                setOnClickListener {
                                    tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                                }
                            }

                            study_next.setOnClickListener {
                                if (index == word_list.size - 1) {
                                    Toast.makeText(this@StudyInActivity, "더 이상 앞으로 갈 수 없습니다. " + response.code().toString(), Toast.LENGTH_LONG).show()
                                } else {
                                    prefManager.wordId = (index + 1)
                                    callWord(index + 1)
                                }
                            }
                            study_back.setOnClickListener {
                                if (index == 0) {
                                    Toast.makeText(this@StudyInActivity, "더 이상 뒤로 갈 수 없습니다. " + response.code().toString(), Toast.LENGTH_LONG).show()
                                } else {
                                    prefManager.wordId = (index - 1)
                                    callWord(index - 1)
                                }
                            }

                            Log.d("word_data", word.toString())
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "단어 로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("word_code", response.code().toString())
                        Log.e("word_err", response.message())
                    }
                }

                override fun onFailure(call: Call<WordData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun callWordList() {
        if (checkNetwork()) {
            setProgressDialog("단어 리스트 로딩 중")

            call_word_list = retrofitService.get_word()
            call_word_list.enqueue(object : Callback<WordListData> {
                override fun onResponse(call: Call<WordListData>?, response: Response<WordListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudyInActivity, "단어 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            word_list = user.list
                            Log.d("word_list_data", word_list.toString())
                            callWord(word_index)
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("word_list_code", response.code().toString())
                        Log.e("word_list_err", response.message())
                    }
                }

                override fun onFailure(call: Call<WordListData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("word_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun setProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean {
        val cm = StudyInActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.isConnectedOrConnecting
    }

    private fun retrofitSetting() {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://dev.juung.me")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }
}
