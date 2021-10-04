package com.shining.webhandler.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import com.shining.webhandler.R
import com.shining.webhandler.databinding.LayoutWebviewBinding
import com.shining.webhandler.webview.WebChromeClientClass
import com.shining.webhandler.webview.WebViewClientClass

/**
 * WebViewFragment.kt
 * WebHandler
 */

class WebViewFragment : BaseFragment() {

    // ViewBinding
    // This property is only valid between onCreateView and onDestroyView.
//    private var _binding : LayoutWebviewBinding? = null
//    private val binding get() = _binding!!
    private lateinit var binding : LayoutWebviewBinding

    private var webView : WebView? = null
    private val github = "https://www.naver.com/"

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
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
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