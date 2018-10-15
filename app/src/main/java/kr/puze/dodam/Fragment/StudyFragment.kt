package kr.puze.dodam.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.actionbar_study.*
import kotlinx.android.synthetic.main.fragment_study.view.*
import kr.puze.dodam.Study.ChatThemeActivity
import kr.puze.dodam.R
import kr.puze.dodam.Study.DebateActivity
import kr.puze.dodam.Study.StudyInActivity
import kr.puze.dodam.Study.SyllableSelectActivity
import android.R.menu
import android.annotation.SuppressLint
import android.widget.PopupMenu
import android.widget.Toast


class StudyFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_study, container, false)

        setting.setOnClickListener {
            var popupMenu = PopupMenu(activity, setting)
            popupMenu.menuInflater.inflate(R.menu.country_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_logout->{
                        Toast.makeText(activity, "Logout", Toast.LENGTH_LONG).show()
                    }
                    R.id.menu_quit->{
                        activity!!.finish()
                    }
                    else -> {
                        Toast.makeText(activity, "ERR", Toast.LENGTH_LONG).show()
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }

        view.study_button_syllable.setOnClickListener {
            startActivity(Intent(activity, SyllableSelectActivity::class.java))
        }
        view.study_button_phonogram.setOnClickListener {
            val intent = Intent(activity, StudyInActivity::class.java)
            intent.putExtra("study", "phonogram")
            startActivity(intent)
        }
        view.study_button_word.setOnClickListener {
            val intent = Intent(activity, StudyInActivity::class.java)
            intent.putExtra("study", "word")
            startActivity(intent)
        }
        view.study_button_chat_bot.setOnClickListener {
            startActivity(Intent(activity, ChatThemeActivity::class.java))
        }
        view.study_button_debate.setOnClickListener {
            startActivity(Intent(activity, DebateActivity::class.java))
        }
        return view
    }

    companion object {
        fun newInstance(): StudyFragment = StudyFragment()
    }
}