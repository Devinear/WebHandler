package com.shining.npermission

import android.content.Context
import android.content.Intent

/**
 * PermissionBuilder.kt
 * WebHandler
 */
abstract class PermissionBuilder<T> {

    private var listener : PermissionListener? = null
    private var permissions : Array<String>? = null
    private var rationaleTitle : String? = null
    private var rationaleMessage : String? = null
    private var rationaleConfirmText : String? = null
    private var denyTitle: String? = null
    private var denyMessage: String? = null
    private var denyCloseText: String? = null

    protected fun checkPermissions(context: Context) {
        requireNotNull(listener) { "You must setPermissionListener() on Permission" }
        if(permissions?.isEmpty() == true) {
            listener!!.onPermissionGranted()
            return
        }

        val intent = Intent(context, PermissionActivity::class.java)

        intent.putExtra(Constants.EXTRA_PERMISSIONS, permissions)
        intent.putExtra(Constants.EXTRA_RATIONALE_TITLE, rationaleTitle)
        intent.putExtra(Constants.EXTRA_RATIONALE_MESSAGE, rationaleMessage)
        intent.putExtra(Constants.EXTRA_RATIONALE_CONFIRM_TEXT, rationaleConfirmText)
        intent.putExtra(Constants.EXTRA_DENY_TITLE, denyTitle)
        intent.putExtra(Constants.EXTRA_DENY_MESSAGE, denyMessage)
        intent.putExtra(Constants.EXTRA_DENIED_DIALOG_CLOSE_TEXT, denyCloseText)
//        intent.putExtra(Constants.EXTRA_PACKAGE_NAME, context.packageName)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)

        PermissionActivity.startActivity(context, intent, listener!!)
    }

    fun permissionListener(listener: PermissionListener) : T {
        this.listener = listener
        return this as T
    }

    fun permissions(permissions: Array<String>) : T {
        this.permissions = permissions
        return this as T
    }

    fun rationaleTitle(title: String) : T {
        this.rationaleTitle = title
        return this as T
    }

    fun rationaleMessage(message: String) : T {
        this.rationaleMessage = message
        return this as T
    }

    fun rationaleConfirmText(text: String) : T {
        this.rationaleConfirmText = text
        return this as T
    }

    fun denyTitle(title: String) : T {
        this.denyTitle = title
        return this as T
    }

    fun denyMessage(message: String) : T {
        this.denyMessage = message
        return this as T
    }

    fun denyCloseText(text: String) : T {
        this.denyCloseText = text
        return this as T
    }

}