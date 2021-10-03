package com.shining.webhandler.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import com.shining.webhandler.R
import com.shining.webhandler.webview.WebChromeClientClass
import com.shining.webhandler.webview.WebViewClientClass

/**
 * WebViewFragment.kt
 * WebHandler
 */

class WebViewFragment : BaseFragment() {

    private var webView : WebView? = null
    private val github = "https://www.naver.com/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.web_view)
        webView?.apply {
            settings.javaScriptEnabled = true
            loadUrl(github)
            webChromeClient = WebChromeClientClass()
            webViewClient = WebViewClientClass()

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
//        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }
}