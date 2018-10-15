package kr.puze.dodam

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#ff5722")
        }
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()

        splash_image.setOnClickListener {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }
}
