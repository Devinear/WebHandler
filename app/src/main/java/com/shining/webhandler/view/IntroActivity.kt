package com.shining.webhandler.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shining.webhandler.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        GlobalScope.launch {
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        const val TAG = "[DE] Intro"
    }
}