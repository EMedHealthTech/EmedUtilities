package com.mohsinali.appUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object CompressFileUtils {
    fun getCompressedImageFile(file: File, mContext: Context, AppName: String): File? {
        try {

            val o: BitmapFactory.Options = BitmapFactory.Options()
            o.inJustDecodeBounds = true

            if (getFileExt(file.name).equals("png") || getFileExt(file.name).equals("PNG") || getFileExt(
                            file.name).equals(
                            "jpg") || getFileExt(file.name).equals("jpeg")) {
                o.inSampleSize = 6
            } else {
                o.inSampleSize = 6
            }

            var inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 100

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2
            }

            val o2: BitmapFactory.Options = BitmapFactory.Options()
            o.inSampleSize = scale
            inputStream = FileInputStream(file)

            var selectedBitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, o2)

            val ei = ExifInterface(file.absolutePath)
            val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    selectedBitmap = rotateImage(selectedBitmap!!, 90F)
                }

                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    selectedBitmap = rotateImage(selectedBitmap!!, 90F)
                }

                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    selectedBitmap = rotateImage(selectedBitmap!!, 90F)
                }

                ExifInterface.ORIENTATION_NORMAL -> {
                }

            }
            inputStream.close()

            // here i override the original image file
            val folder = File(mContext.getExternalFilesDir(null).toString() + "/" + AppName)
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            if (success) {
                val newFile = File(File(folder.absolutePath), file.name)
                if (newFile.exists()) {
                    newFile.delete()
                }
                val outputStream = FileOutputStream(newFile)

                if (getFileExt(file.name).equals("png") || getFileExt(file.name).equals("PNG")) {
                    selectedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                } else {
                    selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                return newFile
            } else {
                return null
            }

        } catch (e: Exception) {
            return null
        }

    }

    fun getFileExt(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1)
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }
}