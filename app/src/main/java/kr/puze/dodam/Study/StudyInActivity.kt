package kr.puze.dodam.Study

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_study_in.*
import kr.puze.dodam.Data.CharData
import kr.puze.dodam.R
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudyInActivity : AppCompatActivity() {

    companion object{
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var call: Call<CharData>
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_in)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        val intent = getIntent()
        val title = intent.getStringExtra("study")

        context = applicationContext
        prefManager = PrefManager(this@StudyInActivity)
        retrofitSetting()

        when(title){
            "syllable"->{
                study_title.text = "Alphabet"
                study_sub.text = "Learn Korean with pictures"
                callSyllable()
            }
            "phonogram"->{
                study_title.text = "Phonetic"
                study_sub.text = "Choose the right pronunciation"
                callPhonetic()
            }
            "word"->{
                study_title.text = "Word"
                study_sub.text = "Choose the right word to fill in the blank"
                callWord()
            }
            else->finish()
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }

    private fun callSyllable(){
        if(checkNetwork()){
            setProgressDialog()
            study_text.visibility = View.GONE
            study_problem_one.text = intent.getStringExtra("syllable")
            study_problem_two.visibility = View.GONE
            study_problem_three.visibility = View.GONE
            study_problem_four.visibility = View.GONE
            val syllableId = intent.getStringExtra("syllable_id")
            call = retrofitService.get_char_id(syllableId)
            call.enqueue(object : Callback<CharData> {
                override fun onResponse(call: Call<CharData>?, response: Response<CharData>?) {
                    progressDialog.dismiss()
                    if (response?.code() == 200) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@StudyInActivity, "로딩 성공 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
                            Log.d("syllable_data", user.list[0].toString())
                        }
                    } else {
                        Toast.makeText(this@StudyInActivity, "로딩 실패 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
                        Log.d("syllable_code", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<CharData>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudyInActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                    Log.d("register_call", t.toString())
                }
            })
        }else{
            Toast.makeText(this@StudyInActivity, "네트워크 연결 실패", Toast.LENGTH_LONG).show()
        }

    }

    private fun callPhonetic(){
        study_image.visibility = View.GONE

    }

    private fun callWord(){
        study_image.visibility = View.GONE

    }

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("회원가입 중")
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean{
        val cm = StudyInActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.isConnectedOrConnecting
    }

    private fun retrofitSetting() {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://dodam.koreacentral.cloudapp.azure.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }
}
