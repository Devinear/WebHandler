package com.shining.webhandler.common

import android.content.Context
import android.content.Context.MODE_PRIVATE

/**
 * Preferences.kt
 * WebHandler
 */
class Preferences(context: Context) {
    private val name = "WebHandler"
    private val prefs = context.getSharedPreferences(name, MODE_PRIVATE)

    var minEnable: Boolean
        get() = prefs.getBoolean(Constants.SHARED_KEY_MIN_ENABLE, false)
        set(value) {
            prefs.edit().putBoolean(Constants.SHARED_KEY_MIN_ENABLE, value).apply()
        }

    var minWidth: Int
        get() = prefs.getInt(Constants.SHARED_KEY_MIN_WIDTH, 700)
        set(value) {
            prefs.edit().putInt(Constants.SHARED_KEY_MIN_WIDTH, value).apply()
        }

    var minHeight: Int
        get() = prefs.getInt(Constants.SHARED_KEY_MIN_HEIGHT, 700)
        set(value) {
            prefs.edit().putInt(Constants.SHARED_KEY_MIN_HEIGHT, value).apply()
        }

    var onlyCurrent: Boolean
        get() = prefs.getBoolean(Constants.SHARED_KEY_ONLY_CURRENT, false)
        set(value) {
            prefs.edit().putBoolean(Constants.SHARED_KEY_ONLY_CURRENT, value).apply()
        }
}