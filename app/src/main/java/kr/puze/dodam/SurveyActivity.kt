package kr.puze.dodam

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_survey.*
import kr.puze.dodam.databinding.ActivitySurveyBinding

class SurveyActivity : AppCompatActivity() {

    private var time: Long = 0
    private var exp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#fafafa")
        }
        setContentView(R.layout.activity_survey)
        supportActionBar!!.hide()

        var exp: Boolean = false
        val binding: ActivitySurveyBinding = DataBindingUtil.setContentView(this, R.layout.activity_survey)
        binding.surveyButtonPositive.setOnClickListener {
            startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
        }
        binding.surveyButtonNegative.setOnClickListener {
            finish()
        }

        val languageStrings = arrayOf("한국어", "English")
        val countryStrings = arrayOf("한국", "United Kingdom")
//        binding.registerSpinnerLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageStrings)
//        binding.registerSpinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                binding.registerTextLanguage.text = languageStrings[position]
//            }
//        }

        survey_exp_true.setOnClickListener {
            exp = true
            survey_exp_true.setBackgroundResource(R.drawable.layout_survey_button)
            survey_exp_true.setTextColor(Color.parseColor("#ff5722"))
            survey_exp_false.setBackgroundResource(R.drawable.layout_survey_button_on)
            survey_exp_false.setTextColor(Color.WHITE)
        }

        survey_exp_false.setOnClickListener {
            exp = false
            survey_exp_false.setBackgroundResource(R.drawable.layout_survey_button)
            survey_exp_false.setTextColor(Color.parseColor("#ff5722"))
            survey_exp_true.setBackgroundResource(R.drawable.layout_survey_button_on)
            survey_exp_true.setTextColor(Color.WHITE)
        }
    }
}