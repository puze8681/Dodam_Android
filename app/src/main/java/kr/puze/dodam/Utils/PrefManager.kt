package kr.puze.dodam.Utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefManager(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()
    var gson = Gson()

    var isLogin: Boolean
        get() = preferences.getBoolean("isLogin", false)
        set(value) {
            editor.putBoolean("isLogin", value)
            editor.apply()
        }

    var access_token: String
        get() = preferences.getString("access_token", "")
        set(value) {
            editor.putString("access_token", value)
            editor.apply()
        }

    var account_id: String
        get() = preferences.getString("account_id", "")
        set(value) {
            editor.putString("account_id", value)
            editor.apply()
        }

    var loginType: Int
        get() = preferences.getInt("type", -1)
        set(value) {
            editor.putInt("type", value)
            editor.apply()
        }

    var userName: String
        get() = preferences.getString("userName", "")
        set(value) {
            editor.putString("userName", value)
            editor.apply()
        }

    var userId: String
        get() = preferences.getString("userId", "")
        set(value) {
            editor.putString("userId", value)
            editor.apply()
        }

    var thirdUserId: String
        get() = preferences.getString("thirdUserId", "")
        set(value) {
            editor.putString("thirdUserId", value)
            editor.apply()
        }

    var userPassword: String
        get() = preferences.getString("userPassword", "")
        set(value) {
            editor.putString("userPassword", value)
            editor.apply()
        }

    var fbToken: String
        get() = preferences.getString("fbToken", "")
        set(value) {
            editor.putString("fbToken", value)
            editor.apply()
        }

    var wordId: Int
        get() = preferences.getInt("wordId", 0)
        set(value) {
            editor.putInt("wordId", value)
            editor.apply()
        }

    var phoneticId: Int
        get() = preferences.getInt("phoneticId", 0)
        set(value) {
            editor.putInt("phoneticId", value)
            editor.apply()
        }

    var checkedLogs: ArrayList<String> = ArrayList()
        get() = gson.fromJson<ArrayList<String>>(preferences.getString("checkedLogs", "[]"), object : TypeToken<ArrayList<String>>() {}.type)
        private set

    fun addCheckedLogs(logId: String) {
        val logs = checkedLogs
        if (!logs.contains(logId)) {
            logs.add(logId)
            editor.putString("checkedLogs", gson.toJson(logs))
            editor.apply()
        }
    }

    fun logout(){
        preferences.edit().clear().commit()
        editor.clear().commit()
    }
}