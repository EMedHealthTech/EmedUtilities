package com.mohsinali.appUtils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mohsinali.interfaces.AlertDialogListener
import com.mohsinali.interfaces.PermissionGrantedListener

object PermissionUtils {
    val mPermissionArrayCameraGallery = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val mPermissionArrayLocation = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun hasCameraAndStoragePermissions(context: Context, requestCode: Int): Boolean {
        val permissionCamera: Int = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val permissionWriteExternal: Int =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionReadExternal: Int =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionReadExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(context as Activity,
                    listPermissionsNeeded.toArray(arrayOf(listPermissionsNeeded.size.toString())), requestCode)
            return false
        }
        return true
    }

    fun hasAudioPermissions(context: Context, requestCode: Int): Boolean {
        val permissionAudio: Int = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        val permissionWriteExternal: Int =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionReadExternal: Int =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionReadExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(context as Activity,
                    listPermissionsNeeded.toArray(arrayOf(listPermissionsNeeded.size.toString())), requestCode)
            return false
        }
        return true
    }

    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun shouldShowAnyRequestPermissionRationale(activity: Activity, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }
        return false
    }

    fun hasLocationPermissions(context: Context, requestCode: Int): Boolean {

        val permissionsNeeded: ArrayList<String> = ArrayList()
        for (permission: String in mPermissionArrayLocation) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission)
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((context as Activity), permissionsNeeded.toTypedArray(), requestCode)
            return false
        }

        return true
    }


    fun onPermissionsResult(mContext: Context, requestCodeResult: Int, requestCodeForCameraAndStorage: Int,
                            grantResults: IntArray,
                            permissionsListener: PermissionGrantedListener) {
        if (requestCodeResult == requestCodeForCameraAndStorage && grantResults.isNotEmpty()) {
            if (isAllPermissionsGranted(grantResults)) {
                permissionsListener.onPermissionGranted()
            } else {
                if (shouldShowAnyRequestPermissionRationale(mContext as Activity, mPermissionArrayCameraGallery)) {
                    DialogUtil.showAlertDialogForEvent(
                            "Alert!",
                            "Go to settings and permissions",
                            "Setting",
                            "Cancel",
                            mContext,
                            object : AlertDialogListener {
                                override fun onAlertDialogEventChanged(isPositive: Boolean) {
                                    if (isPositive) {
                                        openSettingPage(mContext)
                                    }
                                }

                            })
                } else {
                    DialogUtil.showAlertDialogForEvent(
                            "Alert!",
                            "Camera and gallery features required Permission for this app",
                            "Ok",
                            "Cancel",
                            mContext,
                            object : AlertDialogListener {
                                override fun onAlertDialogEventChanged(isPositive: Boolean) {
                                    if (isPositive) {
                                        hasCameraAndStoragePermissions(mContext, requestCodeResult)
                                    }
                                }

                            })
                }
            }
        }

    }

    private fun openSettingPage(mContext: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", mContext.packageName, null)
        intent.data = uri
        mContext.startActivity(intent)
    }
}