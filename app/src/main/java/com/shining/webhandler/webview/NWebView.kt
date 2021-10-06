package com.shining.webhandler.webview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView

/**
 * NWebView.kt
 * WebHandler
 */
class NWebView : WebView {

//    private val nWebChromeClient = WebChromeClientClass()
//    private val nWebViewClient = WebViewClientClass()

    companion object {
        const val TAG = "[DE][SDK] NWebView"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        Log.d(TAG, "init")
        webChromeClient = WebChromeClientClass()
        webViewClient = WebViewClientClass()

//        webChromeClient = nWebChromeClient
//        webViewClient = nWebViewClient
    }

    override fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        Log.d(TAG, "evaluateJavascript")
        super.evaluateJavascript(script, resultCallback)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        Log.d(TAG, "loadUrl")
        super.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadUrl(url: String) {
        Log.d(TAG, "loadUrl")
        super.loadUrl(url)
    }

}