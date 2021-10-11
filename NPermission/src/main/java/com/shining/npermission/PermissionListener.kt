package com.shining.npermission

/**
 * PermissionListener.kt
 * WebHandler
 */
interface PermissionListener {

    fun onPermissionGranted()

    fun onPermissionDenied(deniedPermissions : List<String>)

}