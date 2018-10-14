package kr.puze.dodam.Study

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.actionbar_white.*
import kotlinx.android.synthetic.main.activity_syllable_select.*
import kr.puze.dodam.Adapter.SyllableSelectGridViewAdapter
import kr.puze.dodam.Item.SyllableSelectItem
import kr.puze.dodam.R

class SyllableSelectActivity : AppCompatActivity() {

    var items : ArrayList<SyllableSelectItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_syllable_select)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = Color.parseColor("#fafafa")
        supportActionBar!!.hide()

        var resources: Resources = resources
        var titles: Array<String> = resources.getStringArray(R.array.syllable)
        var subs: Array<String> = resources.getStringArray(R.array.syllable_sub)

        for(i in 0..(titles.size-1)){
            items.add(SyllableSelectItem(titles[i], subs[i]))
            Log.d("SYLLABLE", titles[i]+" "+subs[i]+"\n")
        }

        val adapter = SyllableSelectGridViewAdapter(items)

        syllable_select_grid_view.adapter = adapter
        syllable_select_grid_view.setOnItemClickListener { adapterView, view, i, l ->
            var intent = Intent(this@SyllableSelectActivity, StudyInActivity::class.java)
            intent.putExtra("study", "syllable")
            intent.putExtra("syllable", items.get(i).title)
            startActivity(intent)
        }

        actionbar_back.setOnClickListener {
            finish()
        }
    }
}

