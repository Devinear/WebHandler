package com.shining.webhandler.repository.local

import com.shining.webhandler.common.data.WebData

/**
 * RepositoryListener.kt
 * WebHandler
 */
interface RepositoryListener {
    fun requestAll(list: List<WebData>)
}