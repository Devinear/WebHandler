package com.shining.nwebview

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import java.util.*
import java.util.regex.Pattern

class NWebViewClient(private val webView: NWebView) : WebViewClient() {

    var mListener : NWebListener? = null
//    var mViewClient : WebViewClient? = null

    private var mLastLoadFailed = false

    companion object {
        const val TAG = "[DE][SDK] ViewClient"
    }

    /**
     * url이 웹뷰에서 처리되려고 할 경우에, WebvView에서 노출하지 않고 처리할 수 있음
     * */
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Log.d(TAG, "shouldOverrideUrlLoading URL[$url]")
        view ?: return false
        url ?: return false
        if(url.isBlank()) return false

        // 유효한 URL
        if (!WebViewUtils.isPermittedUrl(view.context, url)) {
            // if a listener is available
            if (mListener != null) {
                // inform the listener about the request
                mListener!!.onExternalPageRequest(url)
            }
            // cancel the original request
            return true
        }

        // route the request through the custom URL loading method
        view.loadUrl(url)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        val hasCookie = CookieManager.getInstance().hasCookies()
        Log.d(TAG, "onPageStarted Cookie[$hasCookie] URL[$url]")
        mLastLoadFailed = false

        if (!webView.hasError()) {
            mListener?.onPageStarted(url, favicon)
        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.d(TAG, "onPageFinished URL[$url]")

        // Myapp
        try {
            // 앱을 종료 후에도 쿠키값이 저장되어 있어 앱을 재실행시 쿠키를 다시 사용 가능함.
            CookieManager.getInstance().flush()
        } catch (e: Exception) {
            Log.e(TAG, "onPageFinished Exception[${e.message}]")
        }
        if (!mLastLoadFailed) {
//            webView.callInjectedJavaScript()
//            webView.linkBridge()
//            emitFinishEvent(webView, url)
        }

        // AA
        if (!webView.hasError()) {
            mListener?.onPageFinished(url)
        }
        super.onPageFinished(view, url)
    }

    /* 페이지 내부의 리소스가 로드가 되면서 다수 호출  */
    override fun onLoadResource(view: WebView?, url: String?) {
        Log.d(TAG, "onLoadResource URL[$url]")
        super.onLoadResource(view, url)
    }

    /* 방문한 링크를 업데이트하는 경우 호출 */
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        Log.d(TAG, "doUpdateVisitedHistory isReload[$isReload] URL[$url]")
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    /**
     * resource request를 가로채서 응답을 내리기 전에 호출되는 메소드
     * 이 메소드를 활용하여 특정 요청에 대한 필터링 및 응답 값 커스텀 가능
     * */
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        Log.d(TAG, "shouldInterceptRequest Request[$request]")
        return super.shouldInterceptRequest(view, request)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        Log.d(TAG, "onFormResubmission")
        super.onFormResubmission(view, dontResend, resend)
    }

    /** 키 입력에 대한 처리
    * return true 동작을 WebView에 위임하지 않는다.
    * */
    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        Log.d(TAG, "shouldOverrideKeyEvent")
        return super.shouldOverrideKeyEvent(view, event)
    }

    /**
     * request 에 대해 에러가 발생했을 때 호출되는 콜백 메소드. error 변수에 에러에 대한 정보가 담겨져있음
     * */
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        Log.d(TAG, "onReceivedError")
//        super.onReceivedError(view, request, error)
        view?: return
        request?: return
        error?: return

        mLastLoadFailed = true
        webView.setLastError()
        mListener?.onPageError(error.errorCode, error.description.toString(), request.url.toString())
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
        Log.d(TAG, "onReceivedSslError")
        super.onReceivedSslError(view, handler, error)
    }

    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        Log.d(TAG, "onReceivedClientCertRequest")
        super.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        Log.d(TAG, "onReceivedHttpAuthRequest Realm[$realm]")
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        Log.d(TAG, "onUnhandledKeyEvent")
        super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        Log.d(TAG, "onScaleChanged")
        super.onScaleChanged(view, oldScale, newScale)
    }

    override fun onReceivedLoginRequest(view: WebView?, realm: String?, account: String?, args: String?) {
        Log.d(TAG, "onReceivedLoginRequest")
        super.onReceivedLoginRequest(view, realm, account, args)
    }
}