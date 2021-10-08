package com.shining.nwebview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi

class WebViewClientClass : WebViewClient() {
    /*
    * https://developer.android.com/reference/android/webkit/WebViewClient.html
    * */

    /* url이 웹뷰에서 처리되려고 할 경우에, WebvView에서 노출하지 않고 처리할 수 있음 */
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

    /* 페이지 내부의 리소스가 로드가 되면서 다수 호출  */
    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    /* 방문한 링크를 업데이트하는 경우 호출 */
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    /* 키 입력에 대한 처리
    * true : 동작을 WebView에 위임하지 않는다. */
    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return super.shouldOverrideKeyEvent(view, event)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
//        super.onReceivedError(view, errorCode, description, failingUrl)
        view?: return
        failingUrl?: return

        onReceivedError(view, failingUrl, errorCode)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
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

    /* WebView에서 ssl을 호출할 경우 인증서 만료, 변조된 인증서의 사용제한하기 위 */
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
    }
}