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
        binding.registerButtonCancel.setOnClickListener {
            finish()
        }
        binding.registerButtonRegister.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, SurveyActivity::class.java))
        }
    }
}
