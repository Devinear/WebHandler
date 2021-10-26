package com.shining.webhandler.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shining.npermission.NPermission
import com.shining.npermission.PermissionListener
import com.shining.webhandler.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    companion object {
        const val TAG = "[DE][AC] Intro"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val listener : PermissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
                Toast.makeText(this@IntroActivity, "Permission Granted", Toast.LENGTH_SHORT).show()

                GlobalScope.launch {
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@IntroActivity, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()

                GlobalScope.launch {
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        NPermission.create()
            .permissionListener(listener)
            .permissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .rationaleTitle("Rationale Title")
            .rationaleMessage("Rationale Message")
            .rationaleConfirmText("Rationale Confirm")
            .denyTitle("Deny Title")
            .denyMessage("Deny Message")
            .denyCloseText("Deny Close")
            .check()
    }
}