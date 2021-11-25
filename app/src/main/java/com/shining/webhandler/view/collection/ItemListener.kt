package com.shining.webhandler.view.collection

import com.shining.webhandler.common.data.ImageData

/**
 * ItemListener.kt
 * WebHandler
 */
interface ItemListener {
    fun clickImageItem(data: ImageData, position: Int)
}
interface ItemLongListener {
    fun longClickImageItem(data: ImageData)
}