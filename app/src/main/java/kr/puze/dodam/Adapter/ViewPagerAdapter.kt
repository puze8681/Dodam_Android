package kr.puze.dodam.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kr.puze.dodam.Fragment.StudyFragment
import kr.puze.dodam.Fragment.MissionBookFragment
import kr.puze.dodam.Fragment.ReportFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ReportFragment.newInstance()
            1 -> StudyFragment.newInstance()
            2 -> MissionBookFragment.newInstance()
            else -> StudyFragment.newInstance()
        }
    }
    override fun getCount(): Int = 3
}