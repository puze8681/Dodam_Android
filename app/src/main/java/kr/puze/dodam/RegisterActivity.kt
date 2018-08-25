package kr.puze.dodam

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kr.puze.dodam.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val binding: ActivityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        val languageStrings = arrayOf("한국어", "English")
        binding.registerSpinnerLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageStrings)
        binding.registerSpinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                binding.registerTextLanguage.text = languageStrings[position]
            }
        }
        binding.registerButtonCancel.setOnClickListener {
            finish()
        }
        binding.registerButtonRegister.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, SurveyActivity::class.java))
        }
    }
}
