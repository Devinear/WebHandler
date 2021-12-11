package com.shining.webhandler.common.data

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.shining.webhandler.common.ImageType

/**
 * ImageData.kt
 * WebHandler
 */
data class ImageData(
    val id: UInt,
    var thumb: Bitmap? = null,
    var image: Bitmap? = null,
    val url: String = "",
    val type: ImageType,
    var checked: Boolean = false,
    var isUpdate: MutableLiveData<Boolean> = MutableLiveData(false)
)
