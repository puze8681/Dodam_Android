package kr.puze.dodam

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kr.puze.dodam.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#fafafa")
        }
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val binding: ActivityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        val languageStrings = arrayOf("한국어", "English")
//        binding.registerSpinnerLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageStrings)
//        binding.registerSpinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                binding.registerTextLanguage.text = languageStrings[position]
//            }
//        }
        binding.registerButtonCancel.setOnClickListener {
            finish()
        }
        binding.registerButtonRegister.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, SurveyActivity::class.java))
        }
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
