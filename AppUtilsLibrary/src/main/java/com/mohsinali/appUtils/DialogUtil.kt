package com.mohsinali.appUtils

import android.R
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.mohsinali.interfaces.AlertDialogListener

object DialogUtil {
    private var progressDialog: ProgressDialog? = null
    private var progressBar: ProgressBar? = null

    fun showAlertDialogForEvent(
            title: String?,
            message: String?,
            positiveButton: String?,
            negativeButton: String?,
            context: Context?,
            callback: AlertDialogListener
    ) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButton) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            callback.onAlertDialogEventChanged(true)
        }
        builder.setNegativeButton(negativeButton) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            callback.onAlertDialogEventChanged(false)
        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }


    fun showAlertDialogForEventCancelable(
            title: String?,
            message: String?,
            positiveButton: String?,
            negativeButton: String?,
            context: Context?,
            callback: AlertDialogListener
    ) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButton) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            callback.onAlertDialogEventChanged(true)
        }
        builder.setNegativeButton(negativeButton) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            callback.onAlertDialogEventChanged(false)
        }
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
    }

    fun showMessageDialog(context: Context, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setPositiveButton(
                context.getString(R.string.ok)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        val dialog = builder.create()
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun showMessageDialog(context: Context, message: String?, Title: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setTitle(Title)
        builder.setPositiveButton(
                context.getString(R.string.ok)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        val dialog = builder.create()
        if (!dialog.isShowing) {
            dialog.show()
        }
    }


    open fun showProgress(mContext: Context, message: String?, isCancelable: Boolean) {
        dismissProgress()
        progressDialog = ProgressDialog(mContext)
        progressDialog!!.setMessage(message ?: "Please wait... ")
        progressDialog!!.setCancelable(isCancelable)
        progressDialog!!.show()
    }

    open fun showProgressBar(progressBar: ProgressBar, view: View) {
        this.progressBar = progressBar
        view.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    open fun showProgressBar(progressBar: ProgressBar) {
        this.progressBar = progressBar
        progressBar.visibility = View.VISIBLE
    }

    open fun dismissProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

    open fun dismissProgressBar(view: View) {
        if (progressBar != null && progressBar!!.visibility == View.VISIBLE) {
            progressBar!!.visibility = View.GONE
            view.visibility = View.VISIBLE
        }
    }

    open fun dismissProgressBar() {
        if (progressBar != null && progressBar!!.visibility == View.VISIBLE) {
            progressBar!!.visibility = View.GONE
        }
    }

}