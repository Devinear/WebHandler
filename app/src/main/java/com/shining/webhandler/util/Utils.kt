package com.shining.webhandler.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import com.shining.webhandler.common.data.ImageData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utils.kt
 * WebHandler
 */
object Utils {

    const val TAG = "[DE] Utils"

    fun startDownload(context: Context, uri: Uri, downloadUrl: String) {
        Log.d(TAG, "startDownload URI : $uri")
        Log.d(TAG, "startDownload Download : $downloadUrl")

        Thread {
            var isSuccess = false
            try {
                val url = URL(downloadUrl)

                //서버와 접속하는 클라이언트 객체 생성
                val connection = url.openConnection() as HttpURLConnection
                val len = connection.contentLength
                val tmpByte = ByteArray(len)
                //입력 스트림을 구한다
                val inputStream = connection.inputStream

                //파일 저장 스트림 생성
                val resolver = context.contentResolver
                val descriptor = resolver.openFileDescriptor(uri, "w")
                val fileDescriptor = descriptor!!.fileDescriptor
                val outputStream = FileOutputStream(fileDescriptor)
                var read: Int
                //입력 스트림을 파일로 저장
                while (true) {
                    read = inputStream.read(tmpByte)
                    if (read <= 0) {
                        break
                    }
                    outputStream.write(tmpByte, 0, read) // file 생성
                }
                inputStream.close()
                outputStream.close()
                connection.disconnect()
                isSuccess = true
            }
            catch (e: Exception) {
                Log.e(TAG,"startDownload Exception : $e")
                e.printStackTrace()
            }
            val msg =
//                if (isSuccess) context.getString(R.string.save_file) else context.getString(R.string.file_download_failed)
                if (isSuccess) "파일을 저장하였습니다." else "파일 다운로드에 실패하였습니다. /n해당 페이지로 이동 합니다."

            (context as Activity).runOnUiThread {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }.start()
    }

//    private static final String DF_GROUP = "yyyy.MM.dd";
//    private static final String DF_PICTURES = "HHmmss";
//    private static final String DF_VIDEOS = "yyyyMMddHHmmss";

    private const val DATE_FORMAT_PICTURES = "HHmmss"

    @SuppressLint("SimpleDateFormat")
    suspend fun imageDownload(context: Context, data: ImageData, name: String = "") {

        val displayName = if (name.isEmpty()) {
            "${SimpleDateFormat(DATE_FORMAT_PICTURES).format(Date())}_${data.id}.jpg"
        } else {
            "$name.jpg"
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val item = contentResolver.insert(collection, values)!!

        contentResolver.openFileDescriptor(item, "w", null).use {
            // write something to OutputStream
            FileOutputStream(it!!.fileDescriptor).use { outputStream ->
//                val imageInputStream = resources.openRawResource(R.raw.my_image)
                val imageInputStream = getImageInputStream(imageData = data) ?: return
                while (true) {
                    val data = imageInputStream.read()
                    if (data == -1) {
                        break
                    }
                    outputStream.write(data)
                }
                imageInputStream.close()
                outputStream.close()
            }
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(item, values, null, null)
    }

    /** * Get Image InputStream
     * @param imageData ImageData
     * @return InputStream from Bitmap
     */
    private fun getImageInputStream(imageData: ImageData) : InputStream? {
        val bytes = ByteArrayOutputStream()
        val bitmap = imageData.image ?: return null

//        val compressType = when (data.type) {
//            ImageType.PNG -> Bitmap.CompressFormat.PNG
//            else -> Bitmap.CompressFormat.JPEG
//        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()
        return ByteArrayInputStream(bitmapData)
    }

    fun dpToPx(context: Context, dp: Float) : Int
            = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()

    fun pxToDp(context: Context, px: Int) : Float =
        when(val density = context.resources.displayMetrics.density) {
            1.0f -> px / 4f
            1.5f -> px / 8f / 3f
            2.0f -> px / 2f
            else -> px / density
        }

}