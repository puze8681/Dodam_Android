package kr.puze.dodam

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import kr.puze.dodam.Adapter.TapLayoutAdapter
import kr.puze.dodam.Adapter.ViewPagerAdapter
import kr.puze.dodam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val tabLayout = TapLayoutAdapter(supportFragmentManager, binding.mainTapLayout.tabCount)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        binding.mainTapLayout.addTab(binding.mainTapLayout.newTab().setText("report").setIcon(R.drawable.ic_report_select).setTag("report"), 0)
        binding.mainTapLayout.addTab(binding.mainTapLayout.newTab().setText("study").setIcon(R.drawable.ic_study_select).setTag("study"), 0)
        binding.mainTapLayout.addTab(binding.mainTapLayout.newTab().setText("missionbook").setIcon(R.drawable.ic_mission_book_select).setTag("missionbook"), 0)
        binding.mainTapLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.mainViewPager.adapter = tabLayout
        binding.mainViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.mainTapLayout))

        binding.mainViewPager.adapter = viewPagerAdapter
        binding.mainViewPager.addOnPageChangeListener(viewPageChangeListener)

        binding.mainTapLayout.setupWithViewPager(binding.mainViewPager)
        initTabLayout()
        binding.mainViewPager.currentItem = 1
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
                    0 -> if(index==position)binding.mainTapLayout.getTabAt(position)!!.setIcon(R.drawable.ic_report_select)
                    1 -> if(index==position)binding.mainTapLayout.getTabAt(position)!!.setIcon(R.drawable.ic_study_select)
                    2 -> if(index==position)binding.mainTapLayout.getTabAt(position)!!.setIcon(R.drawable.ic_mission_book_select)
                }
            }
        }
    }

    private fun initTabLayout() : Unit {
        binding.mainTapLayout.getTabAt(0)?.setIcon(R.drawable.ic_report_unselect)
        binding.mainTapLayout.getTabAt(1)?.setIcon(R.drawable.ic_study_select)
        binding.mainTapLayout.getTabAt(2)?.setIcon(R.drawable.ic_mission_book_unselect)
    }

    fun offTabLayout() : Unit {
        binding.mainTapLayout.getTabAt(0)?.setIcon(R.drawable.ic_report_unselect)
        binding.mainTapLayout.getTabAt(1)?.setIcon(R.drawable.ic_study_unselect)
        binding.mainTapLayout.getTabAt(2)?.setIcon(R.drawable.ic_mission_book_unselect)
    }
}
