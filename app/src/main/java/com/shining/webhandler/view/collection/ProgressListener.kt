package com.shining.webhandler.view.collection

/**
 * ProgressListener.kt
 * WebHandler
 */
interface ProgressListener {
    fun start(max: Int)
    fun update(current: Int, max: Int, url: String = "")
    fun complete()
}