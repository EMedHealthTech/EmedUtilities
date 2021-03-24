package com.mohsinali.appUtils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.common.util.IOUtils
import java.io.*
import java.net.URLEncoder

object FileHelperUtils {
    @SuppressLint("NewApi")
    fun getRealPathFromURI(context: Context, uri: Uri, appName: String): String {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {// DocumentProvider
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(context, uri)
            } else if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split: List<String> = docId.split(":")
                val type: String = split[0]
                // This is for checking Main Memory
                if ("primary".equals(type, ignoreCase = true)) {
                    if (split.size > 1) {
                        return context.getExternalFilesDir(null).toString() + "/" + split[1]
                    } else {
                        return context.getExternalFilesDir(null).toString() + "/"
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/")
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
                parcelFileDescriptor?.let {
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val file = File(context.cacheDir, context.contentResolver.getFileName(uri))
                    val outputStream = FileOutputStream(file)
                    IOUtils.copyStream(inputStream, outputStream)
                    return file.path
                }

                // ToDo add temporory solution to access download folder
                /*val fileName: String? = getFilePath(context, uri)
            var rootPath: String
            if (fileName != null) {
                rootPath = context.getExternalFilesDir(null).toString() + "/Download/" + fileName
                // extraPortion is extra part of file path
                val extraPortion = ("Android/data/" + BuildConfig.APPLICATION_ID
                        + File.separator.toString() + "files" + File.separator)
                // Remove extraPortion
                rootPath = rootPath.replace(extraPortion, "")
                return rootPath
            }*/
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong())
                return getDataColumn(context, contentUri, null.toString(), emptyArray(), appName)
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split: List<String> = docId.split(":")
                val type: String = split[0]
                var contentUri: Uri? = null
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    contentUri = MediaStore.Files.getContentUri("external")
                }
                val selection: String = "_id=?"
                val selectionArgs = arrayOf(split[1])
//                return getRealPathFromUri1(context, contentUri!!)
//                return getDataColumn(context, contentUri, selection, selectionArgs)
                return copyFileToInternalStorage(context, uri, appName)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {// MediaStore (and general)
            // Return the remote address
            // MediaStore (and general)
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment!! else copyFileToInternalStorage(context, uri,
                    appName)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {// File
            return uri.path!!
        }
        return null!!
    }

    fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return URLEncoder.encode(name, "utf-8")
    }

    fun getDataColumn(context: Context, uri: Uri, selection: String, selectionArgs: Array<String>,
                      appName: String): String {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: java.lang.Exception) {
            return getFilePathFromURI(context, uri, appName)
        }
        return null!!
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents".equals(uri.authority)
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage".equals(uri.authority)
                || "com.google.android.apps.docs.storage.legacy".equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents".equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents".equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content".equals(uri.authority)
    }

    fun getFilePathFromURI(context: Context, contentUri: Uri, appName: String): String {
        //copy file and send new file path
        val fileName: String = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(context.getExternalFilesDir(null).toString() + "/" + appName)
            val copyFileName = File(context.getExternalFilesDir(null).toString() + "/" + File.separator + fileName)
            if (!copyFile.exists()) {
                copyFile.mkdir()
            }
            copy(context, contentUri, copyFileName)
            return copyFileName.absolutePath
        }
        return null!!
    }

    fun getFileName(uri: Uri?): String {
        if (uri == null)
            return null!!
        var fileName = ""
        val path: String = uri.path!!
        val cut: Int = path.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    fun copy(context: Context, srcUri: Uri, dstFile: File) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(srcUri)
            if (inputStream == null) return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            IOUtils.copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getDriveFilePath(context: Context, uri: Uri): String {
        val returnUri = uri
        val returnCursor: Cursor? = context.contentResolver.query(returnUri, null, null, null, null)
        /*
   * Get the column indexes of the data in the Cursor,
   *     * move to the first row in the Cursor, get the data,
   *     * and display it.
   * */
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name: String = (returnCursor.getString(nameIndex))
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, URLEncoder.encode(name, "utf-8"))
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            val read: Int = 0
            val maxBufferSize: Int = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()
            //int bufferSize = 1024;
            val bufferSize: Int = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            inputStream.use { inputStream: InputStream ->
                outputStream.use { fileOut ->
                    while (true) {
                        val length = inputStream.read(buffers)
                        if (length <= 0)
                            break
                        fileOut.write(buffers, 0, length)
                    }
                    fileOut.flush()
                    fileOut.close()
                }
            }
            /* while ((inputStream.read(buffers)) != -1) {
             outputStream.write(buffers, 0, read)
         }*/
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
//                outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
        return file.path
    }

    fun getRealPathFromUri1(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun copyFileToInternalStorage(mContext: Context, uri: Uri, newDirName: String): String {
        val returnCursor: Cursor? =
                mContext.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null,
                        null,
                        null)

        /*
 * Get the column indexes of the data in the Cursor,
 *     * move to the first row in the Cursor, get the data,
 *     * and display it.
 * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val output: File
        if (newDirName != "") {
            val dir = File(mContext.filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(mContext.filesDir.toString() + "/" + newDirName + "/" + URLEncoder.encode(name, "utf-8"))
        } else {
            output = File(mContext.filesDir.toString() + "/" + URLEncoder.encode(name, "utf-8"))
        }
        try {
            val inputStream: InputStream? = mContext.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return output.path
    }
}