package kr.puze.dodam.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.ReportData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.FragmentReportBinding
import kr.puze.dodam.databinding.ItemReportBinding

class ReportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_report, container, false)

        val binding: FragmentReportBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)

        var datas = ArrayList<ReportData>()
        var data = ReportData("2018.08.26", "진행현황", "꾸준히 잘 하고 있네요")
        datas.add(data)

        var report = LastAdapter(datas, BR.data_report)
                .map<ReportData, ItemReportBinding>(R.layout.item_report){
                }
                .into(binding.reportRecyclerView)
        return view
    }

    companion object {
        fun newInstance(): ReportFragment = ReportFragment()
    }
}
