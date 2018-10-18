package kr.puze.dodam.Study

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_study_in.*
import kr.puze.dodam.R

class StudyInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_in)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        val intent = getIntent()
        val title = intent.getStringExtra("study")

        when(title){
            "syllable"->{
                study_title.text = "Alphabet"
                study_sub.text = "Learn Korean with pictures"
            }
            "phonogram"->{
                study_title.text = "Phonetic"
                study_sub.text = "Choose the right pronunciation"
            }
            "word"->{
                study_title.text = "Word"
                study_sub.text = "Choose the right word to fill in the blank"
            }
            else->finish()
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }
}
