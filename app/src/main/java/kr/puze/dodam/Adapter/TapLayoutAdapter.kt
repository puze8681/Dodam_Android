package kr.puze.dodam.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import kr.puze.dodam.Fragment.StudyFragment
import kr.puze.dodam.Fragment.MissionBookFragment
import kr.puze.dodam.Fragment.ReportFragment

class TapLayoutAdapter(fm: FragmentManager, private var tapCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                tapCount = position
                return ReportFragment()
            }
            1 -> {
                tapCount = position
                return StudyFragment()
            }
            2 -> {
                tapCount = position
                return MissionBookFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tapCount
    }
}