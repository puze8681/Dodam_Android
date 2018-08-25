package kr.puze.dodam.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import kr.puze.dodam.Fragment.StudyFragment
import kr.puze.dodam.Fragment.MissionBookFragment
import kr.puze.dodam.Fragment.ReportFragment

class TapLayoutAdapter(fm: FragmentManager, private val tapCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return ReportFragment()
                tapCount = position
            }
            1 -> {
                return StudyFragment()
                tapCount = position
            }
            2 -> {
                return MissionBookFragment()
                tapCount = position
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tapCount
    }
}