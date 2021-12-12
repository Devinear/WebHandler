package com.shining.webhandler.view.collection

import com.shining.webhandler.common.data.ImageData

/**
 * ItemListener.kt
 * WebHandler
 */
interface ItemListener<T> {
    fun clickImageItem(data: T, position: Int)
}
interface ItemSizeListener {
    fun changedSize(size: Int)
}
interface ItemLongListener {
    fun longClickImageItem(data: ImageData)
}