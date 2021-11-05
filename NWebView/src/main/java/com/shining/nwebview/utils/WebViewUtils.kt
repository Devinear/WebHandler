package com.shining.nwebview.utils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.shining.nwebview.R
import java.util.regex.Pattern

/**
 * Utils.kt
 * WebHandler
 */
internal object WebViewUtils {

    const val TAG = "[DE][SDK] WebViewUtils"

    var mUrlBlacklist: ArrayList<Any>? = null
    var mUrlWhitelist: List<Pattern>? = null

    fun isPermittedUrl(context: Context, url: String): Boolean {
        Log.d(TAG, "isPermittedUrl URL[$url]")
        // url blacklisting
        if (mUrlBlacklist?.size ?: 0 > 0) {
            for (urlPrefix in mUrlBlacklist!!) {
                if (url.startsWith((urlPrefix as String)))
                    return false
            }
        }
        // url whitelisting
        if (mUrlWhitelist != null && shouldHandleURL(mUrlWhitelist, url)) {
            return false
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return !isUrlScheme(context, url)
        }

        return true
    }

    private fun shouldHandleURL(originWhitelist: List<Pattern>?, url: String): Boolean {
        originWhitelist ?: return false

        Log.d(TAG, "shouldHandleURL URL[$url]")
        val uri = Uri.parse(url)
        val urlToCheck = "${uri.scheme ?: ""}://${uri.authority}"
        for (pattern in originWhitelist) {
            if (pattern.matcher(urlToCheck).matches())
                return true
        }
        return false
    }

    private fun isUrlScheme(context: Context, url: String): Boolean {
        Log.e(TAG, "isUrlScheme URL[$url]")
        val packageName = context.packageName
        val schemeDownload = "//download" // "$packageName://download"
        val schemeBrowser = "//browser" // "$packageName://browser"

        val newUrl = if (url.contains(schemeBrowser) || url.contains(schemeDownload)) {
            if(url.indexOf("://") > 0)
                url.substring(url.indexOf("://") + 1)
            else
                url
        }
        else {
            url
        }
        Log.e(TAG, "isUrlScheme NEW[$newUrl]")

        return try {
            when {
                newUrl.startsWith("intent:") -> return urlIntent(context, url)
                newUrl.startsWith("market://") -> return urlMarket(context, url)
                newUrl.startsWith(schemeDownload) -> return urlDownload(context, url, schemeDownload)
                newUrl.startsWith(schemeBrowser) -> return urlBrowser(context, url, schemeBrowser)
                else -> {
                    Log.e(TAG, "else => $newUrl")
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(newUrl) })
                    true
                }
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "Exception : $e")
            e.printStackTrace()
            false
        }
    }

    private fun urlIntent(context: Context, url: String): Boolean {
        val packageManager = context.packageManager
        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        val intentPackageName = intent.getPackage()
        val existPackage = packageManager.getLaunchIntentForPackage(intentPackageName!!)

        Log.e(TAG, " intentPackageName => $intentPackageName")
        Log.e(TAG, " existPackage => $existPackage")

        try {
            context.startActivity(intent)
            return true
        }
        catch (e: ActivityNotFoundException) {
            val builder = AlertDialog.Builder(context).apply {
                setTitle(context.getText(R.string.app_install_title))
                setMessage(context.getText(R.string.app_install_message))
                setPositiveButton(context.getText(R.string.confirm)) { _, _ ->
                    val marketIntent = Intent(Intent.ACTION_VIEW)
                    marketIntent.data = Uri.parse("market://details?id=$intentPackageName")
                    context.startActivity(marketIntent)
                }
//                setNegativeButton(context.getText(R.string.cancel)) { _, _ -> }
                setCancelable(false)
            }

            val dialog = builder.create()
            dialog.show()
            return true
        }
        catch (e: Exception) {
            Log.e(TAG, "Exception : $e")
            e.printStackTrace()
        }
        return false
    }

    private fun urlMarket(context: Context, url: String): Boolean {
        Log.i(TAG, "market url : $url")
        context.startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME))
        return true
    }

    private fun urlDownload(context: Context, url: String, scheme: String): Boolean {
        Log.i(TAG, "schemeDownload url : $scheme")
        val downloadUrl = url.replace("$scheme?url=", "")
        if (downloadUrl.isNotEmpty()) {
            if (!downloadUrl.contains(scheme)) {
                /**
                 * 다운로드 동작 구현 필요
                 * */

                DownloadUtils.fileDownloadBySAF(context, downloadUrl)

                // Scoped Storage : SAF 저장하는 동작
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

                }
                // 기존 Download 동작
                else {

                }
                Toast.makeText(context, "DOWNLOAD 구현 필요", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun urlBrowser(context: Context, url: String, scheme: String): Boolean {
        Log.i(TAG, "schemeBrowser url : $scheme")
        val browserUrl = url.replace("$scheme?url=", "")
        if (browserUrl.isNotEmpty()) {
            if (!browserUrl.contains(scheme))
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl)))
            else
                Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
        }
        return true
    }
}