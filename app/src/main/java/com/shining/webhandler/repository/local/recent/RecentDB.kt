package com.shining.webhandler.repository.local.recent

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shining.webhandler.repository.local.WebConverters

/**
 * WebRepoDB.kt
 * WebHandler
 */
@Database(entities = [(RecentData::class)], version = 1)
@TypeConverters(WebConverters::class)
abstract class RecentDB : RoomDatabase() {
    abstract fun recentDao() : RecentDao
}