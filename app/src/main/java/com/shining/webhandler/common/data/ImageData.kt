package com.shining.webhandler.common.data

import android.graphics.Bitmap
import com.shining.webhandler.common.ImageType

/**
 * ImageData.kt
 * WebHandler
 */
data class ImageData(
    val id: Int,
    val thumb: Bitmap? = null,
    val image: Bitmap,
    val url: String = "",
    val type: ImageType,
    var checked: Boolean = false
)
