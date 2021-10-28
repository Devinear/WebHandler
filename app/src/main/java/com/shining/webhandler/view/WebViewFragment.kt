package com.shining.webhandler.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shining.nwebview.NWebListener
import com.shining.nwebview.NWebViewClient
import com.shining.webhandler.databinding.LayoutWebviewBinding
import com.shining.webhandler.view.base.BaseFragment

/**
 * WebViewFragment.kt
 * WebHandler
 */
class WebViewFragment : BaseFragment(), NWebListener {

    private lateinit var binding : LayoutWebviewBinding
    private lateinit var viewModel : WebViewViewModel

    private val cUrl = "https://www.naver.com/"

    private val urlShopByLogin = "https://admin.shopby.co.kr/login"
    private val urlG5mdev = "http://m.g5mdev.godomall.com"
    private val urlTest = "https://mobileappdev.godo.co.kr/whysj/urlscheme/index.php"


    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewFragment() }
        val TAG = "[DE][FR] WebView"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = LayoutWebviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        binding.webView.apply {
            settings.useWideViewPort = true     // 화면 맞추기 허용여부
            settings.javaScriptEnabled = true   // javascript 허용여부
            settings.domStorageEnabled = true   // 내부저장소 이용여부

            setListener(this@WebViewFragment, this@WebViewFragment)
            setGeolocationEnabled(false)
            setMixedContentAllowed(false)
            setCookiesEnabled(true)
            setThirdPartyCookiesEnabled(true)
            addHttpHeader("X-Requested-With", "")

//            loadUrl(MainActivity.TEST_PAGE_URL)

            loadUrl(urlTest)

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        binding.webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        binding.webView.onDestroy()
        super.onDestroy()
    }

    fun onWebViewResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onWebViewResultForWebView")
//        super.onActivityResult(requestCode, resultCode, data)
    }

    fun webCanGoBack() : Boolean = binding.webView.canGoBack()

    fun webGoBack() = binding.webView.goBack()

    fun webGoForward() = binding.webView.goForward()


    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        Log.d(TAG, "onPageStarted")
    }

    override fun onPageFinished(url: String?) {
        Log.d(TAG, "onPageFinished")
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        Log.d(TAG, "onPageError")
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        Log.d(TAG, "onDownloadRequested")
    }

    override fun onExternalPageRequest(url: String?) {
        Log.d(TAG, "onExternalPageRequest")
    }
}