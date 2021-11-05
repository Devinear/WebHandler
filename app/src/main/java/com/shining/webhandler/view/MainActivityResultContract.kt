package com.shining.webhandler.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.shining.nwebview.utils.WebViewSetting

/**
 * WebViewResultContract.kt
 * WebHandler
 */
class MainActivityResultContract : ActivityResultContract<Intent, String>() {

//    override fun createIntent(context: Context, input: Intent): Intent =
//        Intent(context, MainActivity::class.java).apply {
//            putExtra("input", input)
//        }
    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): String =
        when (resultCode) {
            Activity.RESULT_OK -> intent?.getStringExtra("result") ?: ""
            else -> ""
        }
}