package com.brainwellnessspa.encryptDecryptUtils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.brainwellnessspa.utility.CONSTANTS.SECRET_KEY

class PrefUtils {
    fun saveSecretKey(value: String?) {
        val editor = myPrefs!!.edit()
        editor.putString(SECRET_KEY, value)
        editor.apply()
    }

    val secretKey: String?
        get() = myPrefs!!.getString(SECRET_KEY, null)

    companion object {
        val prefUtils = PrefUtils()
        var myPrefs: SharedPreferences? = null
        @JvmStatic
        fun getInstance(context: Context?): PrefUtils {
            if (null == myPrefs) myPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefUtils
        }
    }
}