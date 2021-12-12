package com.shining.webhandler.common.data

import android.graphics.Bitmap

/**
 * FavoriteData.kt
 * WebHandler
 */
data class WebData(
    val id: UInt,
    var icon: Bitmap? = null,
    var title: String = "",
    val url: String
    )