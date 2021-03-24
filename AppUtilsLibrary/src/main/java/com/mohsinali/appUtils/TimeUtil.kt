package com.mohsinali.appUtils

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

object TimeUtil {
    fun getDefaultTimeZoneId(): String {
        val tz: TimeZone = TimeZone.getDefault()
        return tz.id
    }
}

