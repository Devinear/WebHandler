package com.shining.webhandler.common.data

import android.graphics.Bitmap

/**
 * WebData.kt
 * WebHandler
 */
data class WebData(val id: Long,
                   var icon: Bitmap? = null,
                   var title: String = "",
                   val url: String,
                   val time: Long)