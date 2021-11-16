package com.shining.webhandler.common.data

import androidx.databinding.ObservableArrayList

/**
 * ImageDataListener.kt
 * WebHandler
 */
interface ImageDataListener {
    fun onItemRangeChanged(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int)
}