package com.shining.nwebview

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.*
import android.webkit.*

internal class NWebChromeClient(private val context: Context, private val webView: NWebView) : WebChromeClient() {

    private var mVideoView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null

    var mChromeClient : WebChromeClient? = null

    companion object {
        const val TAG = "[DE][SDK] ChromeClient"
    }

    /**
     * 커스텀뷰라고 웹뷰를 덮는 형태의 뷰가 보여질때 호출 (ex - youtube) */
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        Log.d(TAG, "onShowCustomView")

        mVideoView?.apply {
            callback?.onCustomViewHidden()
            return
        }

        mVideoView = view
        mCustomViewCallback = callback

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 확인해봐야 함
                (context as Activity).window.setDecorFitsSystemWindows(false)
                val controller = context.window.insetsController
                controller?.apply {
                    hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    // 화면 끝의 스와이프 등 특정 동작시 시스템 바가 나타
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
                }
            } else {
                // KITKAT
                mVideoView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                (context as Activity).window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "onShowCustomView Exception[${e.message}]")
        }

        mVideoView?.setBackgroundColor(Color.BLACK)
        webView.rootView
        webView.visibility = View.GONE
    }

    override fun onHideCustomView() {
        Log.d(TAG, "onHideCustomView")
    }

    /**
     * file upload callback : Android 5.0 (API level 21) */
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?)
            : Boolean {
        Log.d(TAG, "onShowFileChooser")
        return true
    }

    /**
     * 웹뷰내에서 새창을 띄우는 경우 활성화 된다. */
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        Log.e(TAG, "onCreateWindow Dialog[$isDialog] UserGesture[$isUserGesture] ResultMsg[${resultMsg}]")

        view ?: return false
        resultMsg ?: return false

        val newWebView = NWebView(context)
        try {
            val dialog = Dialog(context).apply {
                setContentView(newWebView)

                window ?: return false
                window?.also {
                    it.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                    it.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                    it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }
            dialog.show()

            newWebView.settings.apply {
                builtInZoomControls = true                      // 화면 줌 동작
                displayZoomControls = false                     // 화면 줌 동작시, WebView 에서 줌 컨트롤 표시
                domStorageEnabled = true                        // 내부저장소
                allowFileAccess = false
                allowContentAccess = false
                javaScriptCanOpenWindowsAutomatically = true    // JS 에서 새창 실행
                javaScriptEnabled = true                        // JS 허용여부
                setSupportMultipleWindows(true)                 // 새창 허용여부
                textZoom = 100
                userAgentString = webView.settings.userAgentString
            }

            newWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    Log.d(TAG, "ChildView onCreateWindow shouldOverrideUrlLoading F URL[$url]")
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    Log.d(TAG, "ChildView onCreateWindow shouldOverrideUrlLoading S URL[${request?.url}]")
                    return true
                }
            }
            newWebView.webChromeClient = object : WebChromeClient() {
                override fun onCloseWindow(window: WebView?) {
                    Log.d(TAG, "ChildView onCreateWindow onCloseWindow")
                    dialog.dismiss()
                }
            }

            (resultMsg.obj as WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget();
        }
        catch (e: Exception) {
            Log.e(TAG, "ChildView onCreateWindow Exception[${e.message}]")
        }
        Log.e(TAG, "onCreateWindow end");
        return true
    }

    /**
     * 팝업형태나 Webview의 window가 사라지는 경우 */
    override fun onCloseWindow(window: WebView?) {
        Log.e(TAG, "onCloseWindow")

        window ?: return
        closeWindow(window)
    }

    private fun closeWindow(window: WebView) {
        Log.e(TAG, "closeWindow")
        // 인증 성공 후 window.close(); 가 호출이 되면
        // 해당 함수로 이벤트 발생
        // 네이티브로 새창을 닫아 주는 이벤트 호출
    }

    /**
     * Javascript console message */
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        val debug = BuildConfig.DEBUG
        Log.d(TAG, "onConsoleMessage Debug[$debug]")
        return if (debug) { mChromeClient?.onConsoleMessage(consoleMessage)
            ?: run {
                super.onConsoleMessage(consoleMessage)
            }
        }
        else true
    }

    /**
     * Geolocation API 사용을 위한 팝업 노출 */
    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        Log.d(TAG, "onGeolocationPermissionsShowPrompt")
        if (webView.mGeolocationEnabled) {
            callback.invoke(origin, true, false)
        } else {
            mChromeClient?.onGeolocationPermissionsShowPrompt(origin, callback) ?: run {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        }
    }

    override fun onGeolocationPermissionsHidePrompt() {
        Log.d(TAG, "onGeolocationPermissionsHidePrompt")
        mChromeClient?.onGeolocationPermissionsHidePrompt() ?: run {
            super.onGeolocationPermissionsHidePrompt()
        }
    }

    /**
     * Javascript에서 alert를 이용하여서 팝업을 노출할 경우. 커스텀 가능 */
    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        Log.d(TAG, "onJsAlert")

        AlertDialog.Builder(view.context)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, which ->

            }
            .setCancelable(false)
            .create()
            .show()
        return true
    }

    /**
     * Javascript의 confirm 해당 */
    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        Log.d(TAG, "onJsConfirm")

        AlertDialog.Builder(view.context)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, which ->
                result.confirm()
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
                result.cancel()
            }
            .setCancelable(false)
            .create()
            .show()
        return true
    }

    /**
     * 페이지에서 탐색을 확정하는 대화 상자가 노출된다고 클라이언트에게 알리는 역할
     * Javasciprt의 onbeforeunload()에 해당하며 이곳에서 true를 호출하면
     * 페이지는 탐색을 중지하고 JsResult에서 적절한 값을 호출할 것으로 예상을 하게 됨
     * 기본값 false
     * */
    override fun onJsBeforeUnload(view: WebView, url: String, message: String, result: JsResult): Boolean {
        Log.d(TAG, "onJsBeforeUnload")
        return mChromeClient?.onJsBeforeUnload(view, url, message, result) ?: run {
            super.onJsBeforeUnload(view, url, message, result)
        }
    }

    /**
     * Javascript prompt에 대한하는 기능을 제공 */
    override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
        Log.d(TAG, "onJsPrompt")
        return mChromeClient?.onJsPrompt(view, url, message, defaultValue, result) ?: run {
            super.onJsPrompt(view, url, message, defaultValue, result)
        }
    }

    /**
     * 클라이언트에 권한이 필요할 경우에 호출되는 부분 */
    override fun onPermissionRequest(request: PermissionRequest) {
        Log.d(TAG, "onPermissionRequest")
        mChromeClient?.onPermissionRequest(request) ?: run {
            super.onPermissionRequest(request)
        }
    }

    /**
     * 클라이언트에 권한요청을 취소하는 경우. UI로 노출하여 주었던 부분을 없애주면 됨. */
    override fun onPermissionRequestCanceled(request: PermissionRequest) {
        Log.d(TAG, "onPermissionRequestCanceled")
        mChromeClient?.onPermissionRequestCanceled(request) ?: run {
            super.onPermissionRequestCanceled(request)
        }
    }

    /**
     * 페이지가 로드됨에 따른 퍼센트를 보여주는 부분 */
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        Log.d(TAG, "onProgressChanged Progress[$newProgress]")
        mChromeClient?.onProgressChanged(view, newProgress) ?: run {
            super.onProgressChanged(view, newProgress)
        }
    }

    /**
     * 파비콘이 들어올 경우 호출 */
    override fun onReceivedIcon(view: WebView, icon: Bitmap) {
        Log.d(TAG, "onReceivedIcon")
        mChromeClient?.onReceivedIcon(view, icon) ?: run {
            super.onReceivedIcon(view, icon)
        }
    }

    /**
     * 타이틀이 있는 경우 호출. 네비게이션에 타이틀 넣을때 */
    override fun onReceivedTitle(view: WebView, title: String) {
        Log.d(TAG, "onReceivedTitle Title[$title]")
        mChromeClient?.onReceivedTitle(view, title) ?: run {
            super.onReceivedTitle(view, title)
        }
    }

    /**
     * 애플의 터치 아이콘을 눌렀을 경우 호출
     * https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariWebContent/ConfiguringWebApplications/ConfiguringWebApplications.html
     */
    override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
        Log.d(TAG, "onReceivedTouchIconUrl")
        mChromeClient?.onReceivedTouchIconUrl(view, url, precomposed) ?: run {
            super.onReceivedTouchIconUrl(view, url, precomposed)
        }
    }

    /**
     * 웹뷰의 포커스가 요청될 경우 호출 */
    override fun onRequestFocus(view: WebView) {
        Log.d(TAG, "onRequestFocus")
        mChromeClient?.onRequestFocus(view) ?: run {
            super.onRequestFocus(view)
        }
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        Log.d(TAG, "getDefaultVideoPoster")
        val bitmap = mChromeClient?.getDefaultVideoPoster() ?: run {
            super.getDefaultVideoPoster()
        }
        return bitmap
    }

    override fun getVideoLoadingProgressView(): View? {
        Log.d(TAG, "getVideoLoadingProgressView")
        return mChromeClient?.videoLoadingProgressView ?: run {
            super.getVideoLoadingProgressView()
        }
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String?>?>?) {
        Log.d(TAG, "getVisitedHistory")
        mChromeClient?.getVisitedHistory(callback) ?: run {
            super.getVisitedHistory(callback)
        }
    }
}