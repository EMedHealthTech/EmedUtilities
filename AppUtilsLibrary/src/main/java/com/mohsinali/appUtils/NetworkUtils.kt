package com.mohsinali.appUtils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.mohsinali.appUtils.DialogUtil.showMessageDialog

object NetworkUtils {
    fun isNetworkConnected(context: Context, errorMessage: String): Boolean {

        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {

                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> {
                            showMessageDialog(
                                    context,
                                    errorMessage
                            )
                            false
                        }
                    }
                }
            }
        }
        if (!result) {
            showMessageDialog(
                    context,
                    errorMessage
            )
        }

        return result
    }
}