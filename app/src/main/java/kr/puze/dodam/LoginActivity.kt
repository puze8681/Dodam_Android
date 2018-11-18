package kr.puze.dodam

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kr.puze.dodam.Data.UserData
import kr.puze.dodam.Server.RetrofitService
import kr.puze.dodam.Utils.PrefManager
import kr.puze.dodam.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.ConnectivityManager
import android.view.View
import com.facebook.*
import kr.puze.dodam.Utils.Hasher
import com.facebook.login.LoginResult
import kr.puze.dodam.Data.FacebookUserData


class LoginActivity : AppCompatActivity() {

    private var time: Long = 0

    companion object {
        lateinit var prefManager: PrefManager
        lateinit var progressDialog: ProgressDialog
        lateinit var call: Call<UserData>
        lateinit var fb_call: Call<FacebookUserData>
        lateinit var retrofitService: RetrofitService
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var callbackManager: CallbackManager
        lateinit var token: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#ffffffff")
        }
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginButtonLogin.setOnClickListener {
            if (checkInput()) {
                if (checkNetwork()) {
                    login(login_edit_id.text.toString(), login_edit_pw.text.toString())
                } else {
                    Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "입력창을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
        binding.loginButtonRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        facebook_login.visibility = View.GONE
        callbackManager = CallbackManager.Factory.create()!!

        facebook_login.setReadPermissions("email")
        facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val accessToken: AccessToken = result!!.accessToken
                token = accessToken.token
                Log.d("facebook_login", token)
                login(token)
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
            }

            override fun onCancel() {
                Log.d("facebook_login", "onCancel")
                Toast.makeText(this@LoginActivity, "로그인 취소", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Log.d("facebook_login", "onError")
                error!!.printStackTrace()
                if (checkNetwork()) {
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG).show()
                }
            }
        })

        login_button_facebook.setOnClickListener {
            facebook_login.performClick()
        }

        context = applicationContext
        prefManager = PrefManager(this@LoginActivity)
        retrofitSetting()
        autoLogin()
    }

    private fun autoLogin() {
        if (prefManager.isLogin) {
            when(prefManager.loginType){
                1->{
                    Log.d("login_auto : ", prefManager.userId + prefManager.userPassword)
                    login(prefManager.userId, prefManager.userPassword)
                }
                2->{
                    Log.d("login_auto : ", prefManager.fbToken)
                    login(prefManager.fbToken)
                }
            }

        }
    }

    //fbToken 값을 보냈을때 신규유저면 Survey 로, 기존 유저면 로그인
    private fun login(token: String) {
        setProgressDialog()
        Log.d("login_fun", "running")
        fb_call = retrofitService.post_user_facebook(token)
        fb_call.enqueue(object : Callback<FacebookUserData> {
            override fun onResponse(call: Call<FacebookUserData>?, response: Response<FacebookUserData>?) {
                Log.d("login_call", "onResponse")
                Log.d("login_token", token)
                if (response?.code() == 200) {
                    val user = response.body()
                    if (user != null) {
                        if(user.access_token.isNullOrEmpty()){
                            Log.d("fb_login_user", user.toString())
                            prefManager.userName = user.name
                            prefManager.fbToken = token
                            prefManager.thirdUserId = user.third_user_id
                            prefManager.userId = user.email
                            prefManager.loginType = 0
                            startActivity(Intent(this@LoginActivity, SurveyActivity::class.java))
                            finish()
                        }else{
                            var get_call: Call<UserData> = retrofitService.get_user(user.access_token)
                            get_call.enqueue(object : Callback<UserData> {
                                override fun onResponse(call: Call<UserData>?, response: Response<UserData>?) {
                                    progressDialog.dismiss()
                                    if (response?.code() == 200) {
                                        val fb_user = response.body()
                                        if (fb_user != null) {
                                            prefManager.userName = fb_user.username
                                            prefManager.fbToken = token
                                            prefManager.isLogin = true
                                            prefManager.access_token = user.access_token
                                            prefManager.loginType = 2

                                            intent.putExtra("token", user.access_token)
                                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                            finish()
                                        }
                                    } else {
                                        Toast.makeText(this@LoginActivity, "로그인 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                                        Log.d("login_code", response.code().toString())
                                    }
                                }

                                override fun onFailure(call: Call<UserData>?, t: Throwable?) {
                                    progressDialog.dismiss()
                                    Toast.makeText(this@LoginActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                                    Log.d("login_call", t.toString())
                                }
                            })
                        }
                    }
                } else if (response?.code() == 401) {
                    Log.d("login_call", "401")
                } else {
                    Log.d("login_call", "else")
                    Log.d("login_code", response!!.code().toString())
                }
            }

            override fun onFailure(call: Call<FacebookUserData>?, t: Throwable?) {
                Log.d("login_call", "onFailure")
                Log.d("login_call", t.toString())
            }
        })
    }

    private fun login(id: String, pw: String) {
        setProgressDialog()
        val hash = Hasher()
        val sha_pw = hash.sha256(pw)
        call = retrofitService.post_user_login(id, sha_pw)
        call.enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>?, response: Response<UserData>?) {
                progressDialog.dismiss()
                if (response?.code() == 200) {
                    val user = response.body()
                    if (user != null) {
                        prefManager.userName = "email"
                        prefManager.userId = id
                        prefManager.userPassword = pw
                        prefManager.isLogin = true
                        prefManager.loginType = 1

                        intent.putExtra("token", user.access_token)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패 : " + response!!.code().toString(), Toast.LENGTH_LONG).show()
                    Log.d("login_code", response.code().toString())
                    Log.d("login_result", id + pw)
                }
            }

            override fun onFailure(call: Call<UserData>?, t: Throwable?) {
                progressDialog.dismiss()
                Toast.makeText(this@LoginActivity, "서버 연동 실패", Toast.LENGTH_LONG).show()
                Log.d("login_call", t.toString())
            }
        })
    }

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("로그인 중")
        progressDialog.show()
    }

    private fun checkInput(): Boolean {
        if (login_edit_id.text.toString().isEmpty())
            return false
        if (login_edit_pw.text.toString().isEmpty())
            return false
        return true
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

    override fun onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press the Back button again to exit.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
