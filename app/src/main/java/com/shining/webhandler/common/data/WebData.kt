package com.shining.webhandler.common.data

import android.graphics.Bitmap

/**
 * FavoriteData.kt
 * WebHandler
 */
data class WebData(
    var icon: Bitmap? = null,
    var title: String = "",
    val url: String
    )