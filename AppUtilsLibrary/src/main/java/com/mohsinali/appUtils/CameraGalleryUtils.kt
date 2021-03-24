package com.mohsinali.appUtils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File

object CameraGalleryUtils {
    fun requestToSelectImageFromGalllery(activity: Activity, requestCode: Int) {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.action = Intent.ACTION_GET_CONTENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            intent.type = "image/*|application/pdf"
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "image/*|application/pdf"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += ("$mimeType|")
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Choose Prescription File"),
                requestCode)
    }

    fun isValidFileSize(realImagePath: String): Boolean {
        val file: File = File(realImagePath)
        // Get length of file in bytes
        val fileSizeInBytes: Long = file.length()
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val fileSizeInKB: Long = fileSizeInBytes / 1024
        //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val fileSizeInMB: Long = fileSizeInKB / 1024

        return fileSizeInMB < 8 // MAXIMUM limit of file
    }

    fun getCameraPathFromURI(context: Context, contentUri: Uri): String {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            return ""
        }
    }
}