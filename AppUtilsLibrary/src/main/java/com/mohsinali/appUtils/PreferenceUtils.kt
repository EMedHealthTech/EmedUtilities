package com.mohsinali.appUtils

import android.content.Context
import androidx.preference.PreferenceManager

object PreferenceUtils {
    fun savePreferences(mContext: Context, key: String, value: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value).apply()
    }

    fun getPreferences(context: Context, keyValue: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(keyValue, "")
    }

    fun removeAllSharedPreferences(mContext: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPreferences.edit()
        editor.clear().apply()
    }
}