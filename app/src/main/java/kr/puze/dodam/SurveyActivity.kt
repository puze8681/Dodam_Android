package kr.puze.dodam

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kr.puze.dodam.databinding.ActivitySurveyBinding

class SurveyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        val binding: ActivitySurveyBinding = DataBindingUtil.setContentView(this, R.layout.activity_survey)
        binding.surveyButtonPositive.setOnClickListener {
            startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
        }
        binding.surveyButtonNegative.setOnClickListener {
            startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
        }
    }
}