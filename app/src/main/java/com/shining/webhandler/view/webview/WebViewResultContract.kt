package com.shining.webhandler.view.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * WebViewResultContract.kt
 * WebHandler
 */
class WebViewResultContract : ActivityResultContract<Intent, String>() {

    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): String =
        when (resultCode) {
            Activity.RESULT_OK -> intent?.data.toString()
            else -> ""
        }

}