package kr.puze.dodam.Study

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.StudySelectData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.ActivityStudyInBinding
import kr.puze.dodam.databinding.ItemStudySelectBinding

class StudySelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_select)
        supportActionBar!!.title = "StudySelectActivity"

        val binding: ActivityStudyInBinding = DataBindingUtil.setContentView(this, R.layout.activity_study_select)

        var datas = ArrayList<StudySelectData>()
        var data = StudySelectData("https://lh3.ggpht.com/yVfPv-yLjIuBjpKj41NLkLXmuVv8XzH0m2hf-_sz9lQDv9WB9SX0McB8Jn4bQe4w5Q=s180", "kakao talk", "logo image")
        datas.add(data)

        var study_select = LastAdapter(datas, BR.data_study_select)
                .map<StudySelectData, ItemStudySelectBinding>(R.layout.item_study_select){
                    onClick {
                        var item =it.binding.dataStudySelect
                        startActivity(Intent(this@StudySelectActivity, StudyInActivity::class.java).putExtra("title", item!!.title))
                    }
                }
                .into(binding.studyInRecyclerView)
    }
}
