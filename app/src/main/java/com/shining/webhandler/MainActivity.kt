package com.shining.webhandler

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView

class MainActivity : AppCompatActivity() {

    private var webView : WebView? = null
    private val github = "https://github.com/"

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
        }
    }
}