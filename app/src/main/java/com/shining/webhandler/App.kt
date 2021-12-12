package com.shining.webhandler

import android.app.Application
import androidx.room.Room
import com.shining.webhandler.repository.local.WebRepoDB

/**
 * App.kt
 * WebHandler
 */
open class App : Application() {

    companion object {
        private lateinit var repository: WebRepoDB

        val WEB_DATABASE : WebRepoDB
            get() = repository
    }

    override fun onCreate() {
        super.onCreate()
        repository = Room.databaseBuilder(this, WebRepoDB::class.java, "webs.db").build()
    }
}