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
import kr.puze.dodam.Utils.Hasher
import kr.puze.dodam.Utils.PrefManager
import kr.puze.dodam.databinding.ActivitySurveyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyActivity : AppCompatActivity() {

    var exp: Boolean = true
    var loginType: Int = -1

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

        prefManager = PrefManager(this@SurveyActivity)
        loginType = prefManager.loginType

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
                Log.d("loginType", loginType.toString())
                var fbToken = ""
                var account_type = ""
                var name = ""
                var id = ""
                var pw = ""
                var third = ""
                when(loginType){
                    0-> {
                        account_type = "fb"
                        fbToken = prefManager.fbToken
                        name = prefManager.userName
                        id = prefManager.userId
                        pw = "fb"
                        third = prefManager.thirdUserId
                    }
                    1-> {
                        account_type = "email"
                        fbToken = ""
                        name = intent.getStringExtra("name")
                        id = intent.getStringExtra("id")
                        pw = intent.getStringExtra("pw")
                        third = ""
                    }
                }

                val gender: String
                val country_code = country_code()
                val mother_lang_code = mother_lang_code()
                gender = if(exp){
                    "M"
                }else{
                    "F"
                }
                register(name, id, gender, pw, fbToken, third, country_code, mother_lang_code, account_type, loginType)
            }else{
                Toast.makeText(this@SurveyActivity, "인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG).show()
            }
        }

        context = applicationContext
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

    private fun mother_lang_code(): String{
        val res = resources
        val codes = res.getStringArray(R.array.mother_lang_code)
        val position = survey_spinner_language.selectedItemPosition

        return codes[position]
    }

    private fun register(username: String, id: String, gender: String, pw: String, api_key: String, third_user_id: String, country: String, mother_lang: String, account_type: String, loginType: Int){
        val hash = Hasher()
        val sha_pw = hash.sha256(pw)
        call = retrofitService.post_user(username, id, gender, sha_pw, api_key, third_user_id,country, mother_lang, account_type)
        call.enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>?, response: Response<UserData>?) {
                progressDialog.dismiss()
                if (response?.code() == 200) {
                    val user = response.body()
                    if (user != null) {
                        prefManager.userName = username
                        prefManager.userId = id
                        prefManager.userPassword = pw
                        prefManager.isLogin = true
                        prefManager.loginType = loginType
                        prefManager.access_token = user.access_token
                        prefManager.account_id = user.id

                        intent.putExtra("token", user.access_token)
                        LoginActivity::finish
                        RegisterActivity::finish
                        Toast.makeText(this@SurveyActivity, "회원가입 성공 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@SurveyActivity, "회원가입 실패 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
                    Log.d("register_code", response.code().toString())
                    Log.d("register_result", username+id+gender+pw+api_key+country+mother_lang+account_type)
                    Log.d("register_mother_lang", mother_lang)
                }
            }

            override fun onFailure(call: Call<UserData>?, t: Throwable?) {
                progressDialog.dismiss()
                Toast.makeText(this@SurveyActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                Log.d("register_call", t.toString())
            }
        })
    }

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("회원가입 중")
        progressDialog.show()
    }

    private fun checkNetwork(): Boolean{
        val cm = LoginActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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