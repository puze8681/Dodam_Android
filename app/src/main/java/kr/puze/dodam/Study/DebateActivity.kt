package kr.puze.dodam.Study

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.actionbar_white.*
import kr.puze.dodam.R

class DebateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#ff5722")
        supportActionBar!!.hide()

        actionbar_back.setOnClickListener {
            finish()
        }
    }
}
