package com.shining.nwebview

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView

/**
 * NWebListener.kt
 * WebHandler
 */
interface NWebListener {
    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {}
    fun onPageFinished(view: WebView?, url: String?) {}
    fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {}
    fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {}
    fun onExternalPageRequest(url: String?) {}
    fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?) {}
    fun onReceivedIcon(view: WebView, icon: Bitmap) {}
    fun onReceivedTitle(view: WebView, title: String) {}
}