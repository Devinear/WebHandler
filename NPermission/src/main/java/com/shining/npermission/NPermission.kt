package com.shining.npermission

import android.content.Context

/**
 * NPermission.kt
 * WebHandler
 */
object NPermission {

    fun create(context: Context): NBuilder {
        return NBuilder(context)
    }

    class NBuilder(val context: Context) : PermissionBuilder<NBuilder>() {
        fun check() {
            checkPermissions(context)
        }
    }
}

