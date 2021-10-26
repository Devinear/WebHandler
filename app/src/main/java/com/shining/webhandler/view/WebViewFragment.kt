package com.shining.webhandler.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shining.webhandler.databinding.LayoutWebviewBinding
import com.shining.webhandler.view.base.BaseFragment

/**
 * WebViewFragment.kt
 * WebHandler
 */
class WebViewFragment : BaseFragment() {

    private lateinit var binding : LayoutWebviewBinding
    private lateinit var viewModel : WebViewViewModel

    private val cUrl = "https://www.naver.com/"

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewFragment() }
        val TAG = "[DE][FR] WebView"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            loadUrl(cUrl)

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    fun webCanGoBack() : Boolean = binding.webView.canGoBack()

    fun webGoBack() = binding.webView.goBack()

    fun webGoForward() = binding.webView.goForward()
}