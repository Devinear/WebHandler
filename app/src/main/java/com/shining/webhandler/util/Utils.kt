package com.shining.webhandler.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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

}