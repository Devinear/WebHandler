package com.shining.npermission

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * PermissionActivity.kt
 * WebHandler
 */
class PermissionActivity : AppCompatActivity() {

    private var permissions: Array<String>? = null
    private var rationaleTitle: String? = null
    private var rationaleMessage: String? = null
    private var rationaleConfirmText: String? = null
    private var denyTitle: String? = null
    private var denyMessage: String? = null
    private var denyCloseText: String? = null

    var isShownRationaleDialog = false

    companion object {
        const val REQ_CODE_PERMISSION_REQUEST = 10

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var listener : PermissionListener? = null
        fun startActivity(context: Context, intent: Intent, listener: PermissionListener) {
            this.listener = listener
            this.context = context
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // activity 전환 animation
        overridePendingTransition(0, 0)

        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        setupFromSavedInstanceState(savedInstanceState)

        // 별도처리 필요
        // Manifest.permission.SYSTEM_ALERT_WINDOW

        checkPermissions()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun setupFromSavedInstanceState(savedInstanceState: Bundle?) = when {
        savedInstanceState != null -> {
            permissions = savedInstanceState.getStringArray(Constants.EXTRA_PERMISSIONS)
            rationaleTitle = savedInstanceState.getString(Constants.EXTRA_RATIONALE_TITLE)
            rationaleMessage = savedInstanceState.getString(Constants.EXTRA_RATIONALE_MESSAGE)
            rationaleConfirmText = savedInstanceState.getString(Constants.EXTRA_RATIONALE_CONFIRM_TEXT)
            denyTitle = savedInstanceState.getString(Constants.EXTRA_DENY_TITLE)
            denyMessage = savedInstanceState.getString(Constants.EXTRA_DENY_MESSAGE)
            denyCloseText = savedInstanceState.getString(Constants.EXTRA_DENIED_DIALOG_CLOSE_TEXT)
        }
        else -> {
            val intent = intent
            permissions = intent.getStringArrayExtra(Constants.EXTRA_PERMISSIONS)
            rationaleTitle = intent.getStringExtra(Constants.EXTRA_RATIONALE_TITLE)
            rationaleMessage = intent.getStringExtra(Constants.EXTRA_RATIONALE_MESSAGE)
            rationaleConfirmText = intent.getStringExtra(Constants.EXTRA_RATIONALE_CONFIRM_TEXT)
            denyTitle = intent.getStringExtra(Constants.EXTRA_DENY_TITLE)
            denyMessage = intent.getStringExtra(Constants.EXTRA_DENY_MESSAGE)
            denyCloseText = intent.getStringExtra(Constants.EXTRA_DENIED_DIALOG_CLOSE_TEXT)
        }
    }

    private fun checkPermissions() {
        permissions ?: return
        val needPermissions = ArrayList<String>()

        for (permission in permissions!!) {
            if (isDenied(permission)) {
                needPermissions.add(permission)
            }
        }

        if (needPermissions.isEmpty()) {
            permissionResult(null)
        }
        //Need Show Rationale
        else if (!isShownRationaleDialog && !TextUtils.isEmpty(rationaleMessage)) {
            showRationaleDialog(needPermissions)
        }
        //Need Request Permissions
        else {
            requestPermissions(needPermissions)
        }

    }

    private fun requestPermissions(needPermissions: List<String>) =
        ActivityCompat.requestPermissions(this, needPermissions.toTypedArray(), REQ_CODE_PERMISSION_REQUEST)

    private fun permissionResult(deniedPermissions: List<String>?) {
        finish()
        overridePendingTransition(0, 0)

        listener?: return

        if(deniedPermissions == null || deniedPermissions.isEmpty()) {
            listener?.onPermissionGranted()
        }
        else {
            listener?.onPermissionDenied(deniedPermissions)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val deniedPermissions = ArrayList<String>()
        for(i in grantResults.indices) {
            if(grantResults[i] == PackageManager.PERMISSION_DENIED) // -1
                deniedPermissions.add(permissions[i])
        }

        if(deniedPermissions.isEmpty()) {
            permissionResult(null)
        }
        else {
            showPermissionDenyDialog(deniedPermissions)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun isGranted(permission: String) : Boolean
    = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun isDenied(permission: String) : Boolean
    = !isGranted(permission)

    private fun showRationaleDialog(needPermissions: List<String>) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
            .setTitle(rationaleTitle)
            .setMessage(rationaleMessage)
            .setCancelable(false)
            .setNegativeButton(rationaleConfirmText) { _, _ -> requestPermissions(needPermissions) }
            .show()
        isShownRationaleDialog = true
    }

    private fun showPermissionDenyDialog(deniedPermissions: List<String>) {
        if (TextUtils.isEmpty(denyMessage)) {
            // denyMessage 설정 안함
            permissionResult(deniedPermissions)
            return
        }

        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
            .setTitle(denyTitle)
            .setMessage(denyMessage)
            .setCancelable(false)
            .setNegativeButton(denyCloseText) { _, _ -> permissionResult(deniedPermissions) }
            .show()
    }

}