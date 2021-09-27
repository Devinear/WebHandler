package com.shining.webhandler.webview

import android.graphics.Bitmap
import android.os.Message
import android.view.View
import android.webkit.*

class WebChromeClientClass : WebChromeClient() {

    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    /* 팝업형태나 Webview의 window가 사라지는 경우 */
    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
    }

    /* Javascript console message */
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        return super.onConsoleMessage(consoleMessage)
    }

    /* Geolocation API 사용하는 팝업이 닫힘 */
    override fun onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt()
    }

    /* Geolocation API 사용을 위한 팝업 노출 */
    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    /* Javascript에서 alert를 이용하여서 팝업을 노출할 경우. 커스텀 가능 */
    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        return super.onJsAlert(view, url, message, result)
    }

    /* Javascript의 confirm 해당 */
    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        return super.onJsConfirm(view, url, message, result)
    }

    /* 페이지에서 탐색을 확정하는 대화 상자가 노출된다고 클라이언트에게 알리는 역할
    * Javasciprt의 onbeforeunload()에 해당하며 이곳에서 true를 호출하면
    * 페이지는 탐색을 중지하고 JsResult에서 적절한 값을 호출할 것으로 예상을 하게 됨
    * 기본값 false
    * */
    override fun onJsBeforeUnload(view: WebView, url: String, message: String, result: JsResult): Boolean {
        return super.onJsBeforeUnload(view, url, message, result)
    }

    /* Javascript prompt에 대한하는 기능을 제공 */
    override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }

    /* 클라이언트에 권한이 필요할 경우에 호출되는 부분 */
    override fun onPermissionRequest(request: PermissionRequest) {
        super.onPermissionRequest(request)
    }

    /* 클라이언트에 권한요청을 취소하는 경우. UI로 노출하여 주었던 부분을 없애주면 됨. */
    override fun onPermissionRequestCanceled(request: PermissionRequest) {
        super.onPermissionRequestCanceled(request)
    }

    /* 페이지가 로드됨에 따른 퍼센트를 보여주는 부분 */
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    /* 파비콘이 들어올 경우 호출 */
    override fun onReceivedIcon(view: WebView, icon: Bitmap) {
        super.onReceivedIcon(view, icon)
    }

    /* 타이틀이 있는 경우 호출. 네비게이션에 타이틀 넣을때 */
    override fun onReceivedTitle(view: WebView, title: String) {
        super.onReceivedTitle(view, title)
    }

    /* 애플의 터치 아이콘을 눌렀을 경우 호출
    *  https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariWebContent/ConfiguringWebApplications/ConfiguringWebApplications.html
    *  */
    override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    /* 웹뷰의 포커스가 요청될 경우 호출 */
    override fun onRequestFocus(view: WebView) {
        super.onRequestFocus(view)
    }

    /* 커스텀뷰라고 웹뷰를 덮는 형태의 뷰가 보여질때 호출
    * 예를 들어서 youtube 같은 경우에도 이것에 해당할것 같네요.
    * */
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
    }
}