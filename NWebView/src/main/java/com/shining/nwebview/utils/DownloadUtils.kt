package com.shining.nwebview.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.shining.nwebview.common.DownloadSupportType
import java.text.SimpleDateFormat
import java.util.*

import com.shining.nwebview.R

/**
 * DownloadUtils.kt
 * WebHandler
 */
object DownloadUtils {

    private const val TAG = "[DE][SDK] Download"

    var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    fun fileDownloadBySAF(context: Context, url: String) {
        Log.d(TAG, "fileDownloadBySAF URL[$url]")
        val downloadUrl = url

//        String name = downloadUrl.substring(downloadUrl.lastIndexOf('/') +1);
        val ext: String = downloadUrl.substring(downloadUrl.lastIndexOf('.')) // '.' 포함된 확장자

        // 파일 이름을 날짜 형식으로 변경하여 중복 가능성을 낮춘다.
        val day = Date()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val name = sdf.format(day) + ext

        Log.d(TAG, "fileDownloadBySAF fileName : $name")
        if (name.isEmpty()) return

        var isSupportType = false
        DownloadSupportType.values().forEach { type ->
            if (name.endsWith(".$type", ignoreCase = true)) {
                isSupportType = true
                return@forEach
            }
        }
        if (!isSupportType) {
            Log.d(TAG, "fileDownloadBySAF Not Support")
            val msg = context.getString(R.string.file_type_is_not_support)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

            // 해당 URL 이동
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(downloadUrl)
            context.startActivity(i)
            return
        }

        // Select UI START
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, name)
        }

        val sharedPref = context.getSharedPreferences(WebViewSetting.SHARED_PREF_URL, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(WebViewSetting.DOWNLOAD_URL_TITLE, url)
            commit()
        }
        activityResultLauncher?.launch(intent)
    }

}