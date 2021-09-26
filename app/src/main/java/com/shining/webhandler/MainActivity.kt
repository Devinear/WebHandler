package com.shining.webhandler

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var webView : WebView? = null
//    private val github = "https://github.com/"
    private val github = "https://www.naver.com/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        webView?.apply {
            settings.javaScriptEnabled = true
            loadUrl(github)
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClientClass()

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // WebView BackKey
        if(keyCode == KeyEvent.KEYCODE_BACK && webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}