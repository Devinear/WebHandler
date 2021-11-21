package com.shining.webhandler.common.data

import android.graphics.Bitmap

/**
 * ImageData.kt
 * WebHandler
 */
data class ImageData(
    val id: Int,
    val thumb: Bitmap? = null,
    val image: Bitmap,
    val url: String = ""
)
