package com.shining.webhandler

import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewClientClass : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.loadUrl(url?:"")
        return true
    }
}