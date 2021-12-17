package com.shining.webhandler

import android.app.Application
import androidx.room.Room
import com.shining.webhandler.common.Preferences
import com.shining.webhandler.repository.local.favorite.FavoriteDB
import com.shining.webhandler.repository.local.recent.RecentDB

/**
 * App.kt
 * WebHandler
 */
open class App : Application() {

    companion object {
        private lateinit var favoriteDB: FavoriteDB
        val FAVORITE_DB : FavoriteDB
            get() = favoriteDB

        private lateinit var recentDB : RecentDB
        val RECENT_DB : RecentDB
            get() = recentDB

        lateinit var SHARED: Preferences
    }

    override fun onCreate() {
        super.onCreate()
        favoriteDB = Room.databaseBuilder(this, FavoriteDB::class.java, "favorites.db").build()
        recentDB = Room.databaseBuilder(this, RecentDB::class.java, "recent.db").build()
        SHARED = Preferences(applicationContext)
    }
}