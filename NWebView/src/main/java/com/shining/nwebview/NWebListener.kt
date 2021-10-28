package com.shining.nwebview

import android.graphics.Bitmap

/**
 * NWebListener.kt
 * WebHandler
 */
interface NWebListener {

    fun onPageStarted(url: String?, favicon: Bitmap?)
    fun onPageFinished(url: String?)
    fun onPageError(errorCode: Int, description: String?, failingUrl: String?)
    fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    )
    fun onExternalPageRequest(url: String?)

}