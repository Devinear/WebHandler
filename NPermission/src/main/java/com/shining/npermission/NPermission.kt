package com.shining.npermission

/**
 * NPermission.kt
 * WebHandler
 */
object NPermission {

    fun create(): NBuilder {
        return NBuilder()
    }

    class NBuilder : PermissionBuilder<NBuilder>() {
        fun check() {
            checkPermissions()
        }
    }
}

