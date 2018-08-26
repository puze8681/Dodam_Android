package kr.puze.dodam.Study

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kr.puze.dodam.R

class StudyAnswerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_answer)
        supportActionBar!!.title = "StudyAnswerActivity"

    }
}
