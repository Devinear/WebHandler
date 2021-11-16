package com.shining.webhandler.util

import android.graphics.Bitmap

/**
 * GlideListener.kt
 * WebHandler
 */
interface GlideListener {
    fun onSuccessResource(url: String, bitmap: Bitmap)
    fun onFailureResource(url: String)
}