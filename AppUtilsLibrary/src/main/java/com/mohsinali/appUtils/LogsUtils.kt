package com.mohsinali.appUtils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Field


object LogsUtils {
    private const val DEFAULT_LOG_TAG = "@***@***@"

    fun showError(string: String, context: Context) {
        //val isDebuggable = 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        val isDebuggable = getBuildConfigValue(context, "DEBUG") as Boolean
        if (isDebuggable) {
            Log.e(DEFAULT_LOG_TAG, string)
        }
    }


    fun showError(string: String, logTag: String, buildType: String) {
        if (buildType.equals("DEBUG", ignoreCase = true)) {
            Log.e(logTag, string)
        }
    }

    fun showError(string: Int, buildType: String) {
        if (buildType.equals("DEBUG", ignoreCase = true)) {
            Log.e(DEFAULT_LOG_TAG, string.toString())
        }
    }

    fun showToast(context: Context, string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * @param context       Used to find the correct file
     * @param fieldName     The name of the field-to-access
     * @return              The value of the field, or `null` if the field is not found.
     */
    fun getBuildConfigValue(context: Context, fieldName: String?): Any? {
        try {
            val clazz = Class.forName(context.packageName + ".BuildConfig")
            val field: Field = clazz.getField(fieldName)
            return field.get(null)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }
}
