package com.shining.npermission

import android.app.Application
import android.content.Context

/**
 * PermissionApplication.kt
 * WebHandler
 */
class App : Application() {

    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE : App

        fun getContext() : Context {
            return INSTANCE.applicationContext
        }
    }
}