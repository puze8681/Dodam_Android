package kr.puze.dodam.Study

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import kr.puze.dodam.Data.*
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
    var phonetic_index: Int = 0

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var call_char: Call<CharData>
        lateinit var call_word: Call<WordDataList>
        lateinit var call_word_list: Call<WordListData>
        lateinit var call_phonetic_list: Call<PhoneticListData>
        lateinit var call_phonetic: Call<PhoneticData>
        lateinit var tts: TextToSpeech
        lateinit var word_list: List<WordListIndexData>
        lateinit var phonetic_list: List<PhoneticListIndexData>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_in)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        val intent = intent
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
                phonetic_index = prefManager.phoneticId

                study_image.visibility = View.GONE
                study_text.text = ""
                study_problem_one.text = ""
                study_problem_two.text = ""
                study_problem_three.text = ""
                study_problem_four.text = ""
                study_next.visibility = View.GONE
                study_back.visibility = View.GONE
                callPhoneticList()
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
            Log.d("syllable_id", syllableId)
            call_char = retrofitService.get_char_id(prefManager.access_token, syllableId)
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
                                    .load(user.list[0].image)
                                    .into(study_image)
                            Log.d("syllable_data", user.list[0].image + user.list[0].char)
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

    private fun callPhonetic(index: Int) {
        setProgressDialog("표음 로딩 중")
        Log.d("call_phonetic_index", index.toString())
        study_sub.text = "Chapter." + (index+1).toString() + " Choose the right pronunciation"
        if (checkNetwork()) {
            var phonetic_index = 0

            call_phonetic = retrofitService.get_phonetic_id(prefManager.access_token, phonetic_list[index].id)
            call_phonetic.enqueue(object : Callback<PhoneticData> {
                override fun onResponse(call: Call<PhoneticData>?, response: Response<PhoneticData>?) {
                    progressDialog.dismiss()
                    fun setView(phonetic: PhoneticData, question_index: Int) {
                        study_problem_one.setBackgroundResource(R.drawable.layout_study_in_button)
                        study_problem_one.isEnabled = true
                        study_problem_two.setBackgroundResource(R.drawable.layout_study_in_button)
                        study_problem_two.isEnabled = true
                        study_problem_three.setBackgroundResource(R.drawable.layout_study_in_button)
                        study_problem_three.isEnabled = true
                        study_problem_four.setBackgroundResource(R.drawable.layout_study_in_button)
                        study_problem_four.isEnabled = true
                        var question = phonetic.questions[question_index]
                        var answer = question.answer.number
                        study_text.text = question.sentence
                        study_problem_one.run {
                            study_problem_one.text = question.examples[0]
                            setOnClickListener {
                                tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                                if (answer == 0) {
                                    Toast.makeText(this@StudyInActivity, "정답입니다", Toast.LENGTH_SHORT).show()
                                    val hd = Handler()
                                    hd.postDelayed(Runnable {
                                        if (question_index == phonetic.questions.size-1) {
                                            if (index == phonetic_list.size - 1) {
                                                Toast.makeText(this@StudyInActivity, "더 이상 다음 챕터로 갈 수 없습니다. ", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(this@StudyInActivity, "다음 챕터로 넘어갑니다.", Toast.LENGTH_LONG).show()
                                                prefManager.phoneticId = (index + 1)
                                                callPhonetic(index + 1)
                                            }
                                        } else {
                                            setView(phonetic, question_index+1)
                                        }
                                    }, 2000)
                                } else {
                                    study_problem_one.setBackgroundResource(R.drawable.layout_study_in_button_error)
                                    study_problem_one.isEnabled = false
                                    Toast.makeText(this@StudyInActivity, "오답입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        study_problem_two.run {
                            study_problem_two.text = question.examples[1]
                            setOnClickListener {
                                tts.speak(study_problem_two.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                                if (answer == 1) {
                                    Toast.makeText(this@StudyInActivity, "정답입니다", Toast.LENGTH_SHORT).show()
                                    val hd = Handler()
                                    hd.postDelayed(Runnable {
                                        if (question_index == phonetic.questions.size-1) {
                                            if (index == phonetic_list.size - 1) {
                                                Toast.makeText(this@StudyInActivity, "더 이상 다음 챕터로 갈 수 없습니다. ", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(this@StudyInActivity, "다음 챕터로 넘어갑니다.", Toast.LENGTH_LONG).show()
                                                prefManager.phoneticId = (index + 1)
                                                callPhonetic(index + 1)
                                            }
                                        } else {
                                            setView(phonetic, question_index+1)
                                        }
                                    }, 2000)
                                } else {
                                    study_problem_two.setBackgroundResource(R.drawable.layout_study_in_button_error)
                                    study_problem_two.isEnabled = false
                                    Toast.makeText(this@StudyInActivity, "오답입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        study_problem_three.run {
                            study_problem_three.text = question.examples[2]
                            setOnClickListener {
                                tts.speak(study_problem_three.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                                if (answer == 2) {
                                    Toast.makeText(this@StudyInActivity, "정답입니다", Toast.LENGTH_SHORT).show()
                                    val hd = Handler()
                                    hd.postDelayed(Runnable {
                                        if (question_index == phonetic.questions.size-1) {
                                            if (index == phonetic_list.size - 1) {
                                                Toast.makeText(this@StudyInActivity, "더 이상 다음 챕터로 갈 수 없습니다. ", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(this@StudyInActivity, "다음 챕터로 넘어갑니다.", Toast.LENGTH_LONG).show()
                                                prefManager.phoneticId = (index + 1)
                                                callPhonetic(index + 1)
                                            }
                                        } else {
                                            setView(phonetic, question_index+1)
                                        }
                                    }, 2000)
                                } else {
                                    study_problem_three.setBackgroundResource(R.drawable.layout_study_in_button_error)
                                    study_problem_three.isEnabled = false
                                    Toast.makeText(this@StudyInActivity, "오답입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        study_problem_four.run {
                            study_problem_four.text = question.examples[3]
                            setOnClickListener {
                                tts.speak(study_problem_four.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                                if (answer == 3) {
                                    Toast.makeText(this@StudyInActivity, "정답입니다", Toast.LENGTH_SHORT).show()
                                    val hd = Handler()
                                    hd.postDelayed(Runnable {
                                        if (question_index == phonetic.questions.size-1) {
                                            if (index == phonetic_list.size - 1) {
                                                Toast.makeText(this@StudyInActivity, "더 이상 다음 챕터로 갈 수 없습니다. ", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(this@StudyInActivity, "다음 챕터로 넘어갑니다.", Toast.LENGTH_LONG).show()
                                                prefManager.phoneticId = (index + 1)
                                                callPhonetic(index + 1)
                                            }
                                        } else {
                                            setView(phonetic, question_index+1)
                                        }
                                    }, 2000)
                                } else {
                                    study_problem_four.setBackgroundResource(R.drawable.layout_study_in_button_error)
                                    study_problem_four.isEnabled = false
                                    Toast.makeText(this@StudyInActivity, "오답입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    if (response?.code() == 200) {
                        val phonetic = response.body()
                        if (phonetic != null) {
                            Toast.makeText(this@StudyInActivity, "표음 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            setView(phonetic, 0)
                            Log.d("phonetic_data", phonetic.toString())
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "표음 로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("phonetic_code", response.code().toString())
                        Log.e("phonetic_err", response.message())
                    }
                }

                override fun onFailure(call: Call<PhoneticData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "표음 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("phonetic_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun callPhoneticList() {
        if (checkNetwork()) {
            setProgressDialog("표음 리스트 로딩 중")
            call_phonetic_list = retrofitService.get_phonetic(prefManager.access_token)
            call_phonetic_list.enqueue(object : Callback<PhoneticListData> {
                override fun onResponse(call: Call<PhoneticListData>?, response: Response<PhoneticListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudyInActivity, "표음 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            phonetic_list = user.lectures
                            Log.d("phonetic_list_data", phonetic_list.toString())
                            Log.d("phonetic_list_data_size", phonetic_list.size.toString())
                            callPhonetic(phonetic_index)
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "로딩 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("phonetic_list_code", response.code().toString())
                        Log.e("phonetic_list_err", response.message())
                    }
                }

                override fun onFailure(call: Call<PhoneticListData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("phonetic_list_call", t.toString())
                }
            })
        } else {
            finish()
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun callWord(index: Int) {
        study_sub.text = "Chapter." + (index+1).toString() + " Choose the right word to fill in the blank"
        if (checkNetwork()) {
            setProgressDialog("단어 로딩 중")
            study_text.visibility = View.GONE
            study_problem_two.visibility = View.GONE
            study_problem_three.visibility = View.GONE
            study_problem_four.visibility = View.GONE

            study_problem_one.setOnClickListener {
                tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }

            var word_index = 0

            call_word = retrofitService.get_word_id(prefManager.access_token, word_list[index].id)
            call_word.enqueue(object : Callback<WordDataList> {
                override fun onResponse(call: Call<WordDataList>?, response: Response<WordDataList>?) {
                    progressDialog.dismiss()
                    fun setView(image: String, text: String) {
                        Glide.with(context)
                                .load(image)
                                .into(study_image)
                        study_problem_one.run {
                            //text = word.word
                            study_problem_one.text = text
                            setOnClickListener {
                                tts.speak(study_problem_one.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                            }
                        }
                    }
                    if (response?.code() == 200) {
                        val word = response.body()
                        if (word != null) {
                            Toast.makeText(this@StudyInActivity, "단어 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()

                            setView(word.list[word_index].image, word.list[word_index].word)

                            study_next.setOnClickListener {
                                if (word_index == word.list.size-1) {
                                    if (index == word_list.size - 1) {
                                        Toast.makeText(this@StudyInActivity, "더 이상 다음 챕터로 갈 수 없습니다. " + response.code().toString(), Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@StudyInActivity, "다음 챕터로 넘어갑니다." + response.code().toString(), Toast.LENGTH_LONG).show()
                                        prefManager.wordId = (index + 1)
                                        callWord(index + 1)
                                    }
                                } else {
                                    word_index += 1
                                    setView(word.list[word_index].image, word.list[word_index].word)
                                }
                            }
                            study_back.setOnClickListener {
                                if (word_index == 0) {
                                    if (index == 0) {
                                        Toast.makeText(this@StudyInActivity, "더 이상 이전 챕터로 갈 수 없습니다. " + response.code().toString(), Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@StudyInActivity, "이전 챕터로 넘어갑니다. " + response.code().toString(), Toast.LENGTH_LONG).show()
                                        prefManager.wordId = (index - 1)
                                        callWord(index - 1)
                                    }
                                } else {
                                    word_index -= 1
                                    setView(word.list[word_index].image, word.list[word_index].word)
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

                override fun onFailure(call: Call<WordDataList>?, t: Throwable?) {
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
            call_word_list = retrofitService.get_word(prefManager.access_token)
            call_word_list.enqueue(object : Callback<WordListData> {
                override fun onResponse(call: Call<WordListData>?, response: Response<WordListData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudyInActivity, "단어 리스트 로딩 성공 : " + response.code().toString(), Toast.LENGTH_LONG).show()
                            word_list = user.list
                            Log.d("word_list_data", word_list.toString())
                            Log.d("word_list_data_size", word_list.size.toString())
                            Log.d("word_index", word_index.toString())
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
                .baseUrl("http://api.dodam.io")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }
}