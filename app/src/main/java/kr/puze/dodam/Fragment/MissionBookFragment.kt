package kr.puze.dodam.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import kr.puze.dodam.BR
import kr.puze.dodam.Data.MissionBookData
import kr.puze.dodam.R
import kr.puze.dodam.databinding.FragmentMissionBookBinding
import kr.puze.dodam.databinding.ItemMissionBookBinding

class MissionBookFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_mission_book, container, false)

        val binding: FragmentMissionBookBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mission_book, container, false)

        var datas = ArrayList<MissionBookData>()
        var data = MissionBookData("출석체크", "5 / 10", 10, 5)
        datas.add(data)

        var missionBook = LastAdapter(datas, BR.data_mission_book)
                .map<MissionBookData, ItemMissionBookBinding>(R.layout.item_mission_book){
                }
                .into(binding.missionBookRecyclerView)
        return view
    }

    companion object {
        fun newInstance(): MissionBookFragment = MissionBookFragment()
    }
}