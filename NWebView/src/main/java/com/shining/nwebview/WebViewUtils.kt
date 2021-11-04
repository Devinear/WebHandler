package com.shining.nwebview

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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

        val newUrl =
            if(url.indexOf("://") > 0) { url.substring(url.indexOf("://") + 1) }
            else { url }
        Log.e(TAG, "isUrlScheme NEW[$url]")

        val packageName = context.packageName
        val schemeDownload = "//download" // "$packageName://download"
        val schemeBrowser = "//browser" // "$packageName://browser"

        return try {
            val packageManager = context.packageManager
            when {
                newUrl.startsWith("intent:") -> {
                    val intent = Intent.parseUri(newUrl, Intent.URI_INTENT_SCHEME)
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
//                            setNegativeButton(view.context.getText(R.string.cancel)) { _, _ -> }
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
                    false
                }
                // 마켓
                newUrl.startsWith("market://") -> {
                    Log.i(TAG, "market url : $newUrl")
                    context.startActivity(Intent.parseUri(newUrl, Intent.URI_INTENT_SCHEME))
                    true
                }
                // 다운로드
                newUrl.startsWith(schemeDownload) -> {
                    Log.i(TAG, "schemeDownload url : $schemeDownload")
                    val downloadUrl = newUrl.replace("$schemeDownload?url=", "")
                    if (downloadUrl.isNotEmpty()) {
                        if (!downloadUrl.contains(schemeDownload)) {
                            /**
                             * 다운로드 동작 구현 필요
                             * */
                            Toast.makeText(context, "DOWNLOAD 구현 필요", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                // 시스템 브라우져
                newUrl.startsWith(schemeBrowser) -> {
                    Log.i(TAG, "schemeBrowser url : $schemeBrowser")
                    val browserUrl = newUrl.replace("$schemeBrowser?url=", "")
                    if (browserUrl.isNotEmpty()) {
                        if (!browserUrl.contains(schemeBrowser))
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl)))
                        else
                            Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, context.getString(R.string.invalid_link), Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> {
                    Log.i(TAG, "else url : $schemeBrowser")
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
}