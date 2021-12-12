package com.shining.webhandler.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shining.webhandler.common.data.WebData

/**
 * WebRepoDB.kt
 * WebHandler
 */
@Database(entities = [(WebRepoData::class)], version = 1)
@TypeConverters(WebConverters::class)
abstract class WebRepoDB : RoomDatabase() {
    abstract fun webRepoDao() : WebRepoDao
}