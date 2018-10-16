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
import kr.puze.dodam.Utils.Hasher


class LoginActivity : AppCompatActivity() {

    private var time: Long = 0

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
            window.statusBarColor = Color.parseColor("#ffffffff")
        }
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginButtonLogin.setOnClickListener {
            if(checkInput()){
                if(checkNetwork()){
                    setProgressDialog()
                    login(login_edit_id.text.toString(), login_edit_pw.text.toString())
                    finish()
                }else{
                    Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this@LoginActivity, "입력창을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
        binding.loginButtonRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        context = applicationContext
        prefManager = PrefManager(this@LoginActivity)
        autoLogin()
        retrofitSetting()
    }

    private fun autoLogin(){
        if(prefManager.isLogin){
            login(prefManager.userId, prefManager.userPassword)
        }
    }

    private fun login(id: String, pw: String) {
        val hash = Hasher()
        val sha_pw = hash.sha256(pw)
        call = retrofitService.post_user_login(id,sha_pw)
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
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패 : "+response!!.code().toString(), Toast.LENGTH_LONG).show()
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

    private fun checkNetwork(): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.isConnectedOrConnecting
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

    override fun onBackPressed() {
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
        }
    }
}
