package com.mohsinali.appUtils

import android.text.TextUtils

object ValidationUtils {
    fun getFirstLetterCapital(word: String): String {
        return if (!TextUtils.isEmpty(word)) word.substring(0, 1).toUpperCase() + word.substring(1) else ""
    }
}