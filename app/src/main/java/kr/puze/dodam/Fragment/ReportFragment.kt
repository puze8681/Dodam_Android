package kr.puze.dodam.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.puze.dodam.R

class ReportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_report, container, false)
    }

    companion object {
        fun newInstance(): ReportFragment = ReportFragment()
    }
}
