package com.shining.webhandler.common.listener

import androidx.databinding.ObservableArrayList

/**
 * ImageDataListener.kt
 * WebHandler
 */
interface DataListener<T> {
    fun onItemRangeChanged(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {}
    fun onChanged(sender: ObservableArrayList<T>) {}
    fun onItemRangeInserted(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {}
    fun onItemRangeMoved(sender: ObservableArrayList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {}
    fun onItemRangeRemoved(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {}
    fun onChanged(sender: List<T>)
}