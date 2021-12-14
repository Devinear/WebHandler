package com.shining.webhandler.repository.local.favorite

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shining.webhandler.repository.local.WebConverters

/**
 * WebRepoDB.kt
 * WebHandler
 */
@Database(entities = [(FavoriteData::class)], version = 1)
@TypeConverters(WebConverters::class)
abstract class FavoriteDB : RoomDatabase() {
    abstract fun favoriteDao() : FavoriteDao
}