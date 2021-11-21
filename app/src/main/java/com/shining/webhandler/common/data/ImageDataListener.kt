package com.shining.webhandler.common.data

import androidx.databinding.ObservableArrayList

/**
 * ImageDataListener.kt
 * WebHandler
 */
interface ImageDataListener {
    fun onItemRangeChanged(sender: ObservableArrayList<ImageData>, positionStart: Int, itemCount: Int) {}
    fun onChanged(sender: ObservableArrayList<ImageData>) {}
    fun onItemRangeInserted(sender: ObservableArrayList<ImageData>, positionStart: Int, itemCount: Int) {}
    fun onItemRangeMoved(sender: ObservableArrayList<ImageData>, fromPosition: Int, toPosition: Int, itemCount: Int) {}
    fun onItemRangeRemoved(sender: ObservableArrayList<ImageData>, positionStart: Int, itemCount: Int) {}
    fun onChanged(sender: List<ImageData>)
}