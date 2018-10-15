package kr.puze.dodam

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_survey.*
import kr.puze.dodam.Data.UserData
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import kr.puze.dodam.databinding.ActivitySurveyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyActivity : AppCompatActivity() {

    var exp: Boolean = true

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var call: Call<UserData>
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#fafafa")
        }
        setContentView(R.layout.activity_survey)
        supportActionBar!!.hide()

        val binding: ActivitySurveyBinding = DataBindingUtil.setContentView(this, R.layout.activity_survey)

        binding.surveyButtonNegative.setOnClickListener {
            finish()
        }

        survey_gender_male.setOnClickListener {
            exp = true
            survey_gender_female.setBackgroundResource(R.drawable.layout_survey_button)
            survey_gender_female.setTextColor(Color.parseColor("#ff5722"))
            survey_gender_male.setBackgroundResource(R.drawable.layout_survey_button_on)
            survey_gender_male.setTextColor(Color.WHITE)
        }

        survey_gender_female.setOnClickListener {
            exp = false
            survey_gender_male.setBackgroundResource(R.drawable.layout_survey_button)
            survey_gender_male.setTextColor(Color.parseColor("#ff5722"))
            survey_gender_female.setBackgroundResource(R.drawable.layout_survey_button_on)
            survey_gender_female.setTextColor(Color.WHITE)
        }

        binding.surveyButtonPositive.setOnClickListener {
            if(checkNetwork()){
                setProgressDialog()
                val name = intent.getStringExtra("name")
                val id = intent.getStringExtra("id")
                val pw = intent.getStringExtra("pw")
                val gender: String
                val code = country_code()
                val mother_lang = survey_spinner_language.selectedItem.toString()
                if(exp){
                    gender = "M"
                }else{
                    gender = "F"
                }
                register(name, gender, id, pw, code, mother_lang, "email")
                finish()
            }else{
                Toast.makeText(this@SurveyActivity, "인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG).show()
            }
            startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
        }

        prefManager = PrefManager(this@SurveyActivity)
        genderSetting()
        retrofitSetting()
    }

    private fun genderSetting(){
        exp = true
        survey_gender_female.setBackgroundResource(R.drawable.layout_survey_button)
        survey_gender_female.setTextColor(Color.parseColor("#ff5722"))
        survey_gender_male.setBackgroundResource(R.drawable.layout_survey_button_on)
        survey_gender_male.setTextColor(Color.WHITE)
    }

    private fun country_code(): String{
        val res = resources
        val codes = res.getStringArray(R.array.country_code)
        val position = survey_spinner_country.selectedItemPosition

        return codes[position]
    }

    private fun register(name: String, gender: String, id: String, pw: String, country: String, mother_lang: String, account_type: String){
        call = retrofitService.post_users(name, gender, id, pw, country, mother_lang, account_type)
        call.enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>?, response: Response<UserData>?) {
                progressDialog.dismiss()
                if (response?.code() == 200) {
                    val user = response.body()
                    if (user != null) {
                        prefManager.userId = id
                        prefManager.userPassword = pw
                        prefManager.isLogin = true

                        intent.putExtra("token", user.access_token)
                        LoginActivity::finish
                        RegisterActivity::finish
                        startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@SurveyActivity, "회원가입 실패 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
                    Log.d("login_code", response.code().toString())
                }
            }

            override fun onFailure(call: Call<UserData>?, t: Throwable?) {
                progressDialog.dismiss()
                Toast.makeText(this@SurveyActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                Log.d("login_call", t.toString())
            }
        })
    }

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("회원가입 중")
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun retrofitSetting() {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://dodam.koreacentral.cloudapp.azure.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }
}