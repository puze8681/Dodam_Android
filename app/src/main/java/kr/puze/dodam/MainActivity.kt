package kr.puze.dodam

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import kr.puze.dodam.Adapter.TapLayoutAdapter
import kr.puze.dodam.Adapter.ViewPagerAdapter
import kr.puze.dodam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val tabLayout = TapLayoutAdapter(supportFragmentManager, main_tap_layout.tabCount)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        main_tap_layout.addTab(main_tap_layout.newTab().setText("report").setIcon(R.drawable.ic_report_select).setTag("report"), 0)
        main_tap_layout.addTab(main_tap_layout.newTab().setText("study").setIcon(R.drawable.ic_study_select).setTag("study"), 0)
        main_tap_layout.addTab(main_tap_layout.newTab().setText("missionbook").setIcon(R.drawable.ic_mission_book_select).setTag("missionbook"), 0)
        main_tap_layout.tabGravity = TabLayout.GRAVITY_FILL

        main_view_pager.adapter = tabLayout
        main_view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(main_tap_layout))

        main_view_pager.adapter = viewPagerAdapter
        main_view_pager.addOnPageChangeListener(viewPageChangeListener)

        main_tap_layout.setupWithViewPager(main_view_pager)
        initTabLayout()
        main_view_pager.currentItem = 1
    }
    private val viewPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            offTabLayout()
            for (index in 0..3) {
                when (index) {
                    0 -> if(index==position)main_tap_layout.getTabAt(position)!!.setIcon(R.drawable.ic_report_select)
                    1 -> if(index==position)main_tap_layout.getTabAt(position)!!.setIcon(R.drawable.ic_study_select)
                    2 -> if(index==position)main_tap_layout.getTabAt(position)!!.setIcon(R.drawable.ic_mission_book_select)
                }
            }
        }
    }

    private fun initTabLayout() : Unit {
        main_tap_layout.getTabAt(0)?.setIcon(R.drawable.ic_report_unselect)
        main_tap_layout.getTabAt(1)?.setIcon(R.drawable.ic_study_select)
        main_tap_layout.getTabAt(2)?.setIcon(R.drawable.ic_mission_book_unselect)
    }

    fun offTabLayout() : Unit {
        main_tap_layout.getTabAt(0)?.setIcon(R.drawable.ic_report_unselect)
        main_tap_layout.getTabAt(1)?.setIcon(R.drawable.ic_study_unselect)
        main_tap_layout.getTabAt(2)?.setIcon(R.drawable.ic_mission_book_unselect)
    }
}
