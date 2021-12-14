package com.shining.webhandler.repository.local

import com.shining.webhandler.common.data.WebData

/**
 * BaseRepository.kt
 * WebHandler
 */
abstract class BaseRepository {
    abstract fun insert(data: WebData)
    abstract fun remove(id: Long)
    abstract fun getAll(listener: RepositoryListener)
}