package kr.puze.dodam.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_study.view.*
import kr.puze.dodam.R
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.actionbar_study.view.*
import kr.puze.dodam.LoginActivity
import kr.puze.dodam.Study.*
import kr.puze.dodam.Utils.PrefManager


class StudyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_study, container, false)

        prefManager = PrefManager(this.activity!!)
        view.actionbar_setting.setOnClickListener {
            Log.d("pop", "setting click")
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

            builder.setTitle("Setting")
            builder.setMessage("Select what to do")
            builder.setNeutralButton("Log out") { dialogInterface, i ->
                Toast.makeText(activity, "Logout", Toast.LENGTH_LONG).show()
                prefManager.logout()
                startActivity(Intent(activity!!, LoginActivity::class.java))
                activity!!.finish()
            }
            builder.setPositiveButton("Exit") { dialogInterface, i -> activity!!.finish() }
            builder.setNegativeButton("Cancel", null)
            builder.setCancelable(true)
            builder.create()
            builder.show()
        }

        view.study_button_syllable.setOnClickListener {
            val intent = Intent(activity, StudySelectActivity::class.java)
            intent.putExtra("study", "syllable")
            startActivity(intent)
        }
        view.study_button_phonogram.setOnClickListener {
            val intent = Intent(activity, StudySelectActivity::class.java)
            intent.putExtra("study", "phonogram")
            startActivity(intent)
        }
        view.study_button_word.setOnClickListener {
            val intent = Intent(activity, StudySelectActivity::class.java)
            intent.putExtra("study", "word")
            startActivity(intent)
        }
        view.study_button_chat_bot.setOnClickListener {
            val intent = Intent(activity, ChatThemeActivity::class.java)
            intent.putExtra("token", intent.getStringExtra("token"))
            startActivity(intent)
        }
        view.study_button_debate.setOnClickListener {
            startActivity(Intent(activity, DebateThemeActivity::class.java))
        }
        return view
    }

    companion object {
        fun newInstance(): StudyFragment = StudyFragment()
        lateinit var prefManager: PrefManager
    }
}