package com.shining.webhandler.view.webview

import android.webkit.JavascriptInterface
import org.jsoup.Jsoup

/**
 * JavascriptInterface.kt
 * WebHandler
 */
class JavascriptInterface(private val viewModel: WebViewViewModel) {

    @JavascriptInterface
    fun getHtml(html: String) {

        // HTML
        val doc = Jsoup.parse(html)

        // TAG img > ATTR src - Image url 추
        for (img in doc.select("img")) {
            val src = img.attr("src")
            src ?: continue

            viewModel.addUrl(src)
        }
    }

    @JavascriptInterface
    fun getImgSrc(url: String) {

        // Img SRC
        if(url.startsWith("http")) {
            viewModel.addUrl(url)
        }
    }
}