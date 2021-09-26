package com.shining.webhandler.webview

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

class WebViewClientClass : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.loadUrl(url?:"")
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
//        super.onReceivedError(view, errorCode, description, failingUrl)
        view?: return
        failingUrl?: return

        onReceivedError(view, failingUrl, errorCode)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
//        super.onReceivedError(view, request, error)
        view?: return
        request?: return
        error?: return

        onReceivedError(view, request.url.toString(), error.errorCode)
    }

    private fun onReceivedError(view: WebView, failingUrl: String, errorCode: Int) {
        when (errorCode) {
            // 서버에서 사용자 인증 실패
            ERROR_AUTHENTICATION -> { }
            // 잘못된 URL
            ERROR_BAD_URL -> { }
            // 서버로 연결 실패
            ERROR_CONNECT -> { }
            // SSL handshake 수행 실패
            ERROR_FAILED_SSL_HANDSHAKE -> { }
            // 일반 파일 오류
            ERROR_FILE -> { }
            // 파일을 찾을 수 없습니다
            ERROR_FILE_NOT_FOUND -> { }
            // 서버 또는 프록시 호스트 이름 조회 실패
            ERROR_HOST_LOOKUP -> { }
            // 서버에서 읽거나 서버로 쓰기 실패
            ERROR_IO -> { }
            // 프록시에서 사용자 인증 실패
            ERROR_PROXY_AUTHENTICATION -> { }
            // 너무 많은 리디렉션
            ERROR_REDIRECT_LOOP -> { }
            // 연결 시간 초과
            ERROR_TIMEOUT -> { }
            // 페이지 로드중 너무 많은 요청 발생
            ERROR_TOO_MANY_REQUESTS -> { }
            // 일반 오류
            ERROR_UNKNOWN -> { }
            // 지원되지 않는 인증 체계
            ERROR_UNSUPPORTED_AUTH_SCHEME -> { }
            // URI가 지원되지 않는 방식
            ERROR_UNSUPPORTED_SCHEME -> { }
        }
    }
}