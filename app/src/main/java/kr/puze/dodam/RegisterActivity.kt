package kr.puze.dodam

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import kr.puze.dodam.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

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
            if(checkInput()){
                val registerIntent = Intent(this@RegisterActivity, SurveyActivity::class.java)
                registerIntent.putExtra("name", register_edit_name.text.toString())
                registerIntent.putExtra("id", register_edit_email.text.toString())
                registerIntent.putExtra("pw", register_edit_pw_check.text.toString())
                startActivity(registerIntent)
            }else{
                Toast.makeText(applicationContext, "입력창을 확인 해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkInput(): Boolean {
        if (register_edit_name.text.toString().isEmpty())
            return false
        if (register_edit_email.text.toString().isEmpty())
            return false
        if (register_edit_pw.text.toString().isEmpty())
            return false
        if (register_edit_pw_check.text.toString().isEmpty())
            return false
        if (register_edit_pw.text.toString() != register_edit_pw_check.text.toString())
            return false
        return true
    }
}
