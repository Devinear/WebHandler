package com.shining.webhandler.view.webview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shining.nwebview.NWebListener
import com.shining.nwebview.utils.WebViewSetting
import com.shining.webhandler.databinding.LayoutWebviewBinding
import com.shining.webhandler.util.Utils
import com.shining.webhandler.view.base.BaseFragment


/**
 * WebViewFragment.kt
 * WebHandler
 */
class WebViewFragment : BaseFragment(), NWebListener {

    private lateinit var binding : LayoutWebviewBinding

    // Kotlin 위임(by) 활용, 초기화되는 Activity 또는 Fragment Lifecycle 종속됨
    private val viewModel : WebViewViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T
                    = WebViewViewModel(requireActivity()) as T
        }
    }

    private val cUrl = "https://www.naver.com/"

    private val urlShopByLogin = "https://admin.shopby.co.kr/login"
    private val urlG5mdev = "http://m.g5mdev.godomall.com"
    private val urlTest = "https://mobileappdev.godo.co.kr/whysj/urlscheme/index.php"

    var userAgent: String? = ""

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewFragment() }
        const val TAG = "[DE][FR] WebView"
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        binding.webView.settings.apply {
            builtInZoomControls = true                      // 화면 줌 동작
            displayZoomControls = false                     // 화면 줌 동작시, WebView 에서 줌 컨트롤 표시
            domStorageEnabled = true                        // 내부저장소
            allowFileAccess = false                         // File 엑세스
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = true    // JS 에서 새창 실행
            javaScriptEnabled = true                        // JS 허용여부
            setSupportMultipleWindows(false)                // 새창 허용여부 - false : 현재 창에 로드

            textZoom = 100
            userAgentString = userAgent ?: ""
            loadWithOverviewMode = true                     // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

            // Main WebView Only
            loadWithOverviewMode = true
            useWideViewPort = true                          // 화면 맞추기 허용여부(html의 viewport)
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // 브라우저 캐시 사용 재정

            // Deprecated
            allowUniversalAccessFromFileURLs = false
        }

        binding.webView.apply {
            setListener(this@WebViewFragment, this@WebViewFragment)
            addHttpHeader("X-Requested-With", "")

//            loadUrl("https://topegirl.com/watch/dakota-pink-nude-seduce-120-photos.html")
            loadUrl("https://xhwebsite.com/photos/gallery/the-beauty-of-blonde-1427226")
//            loadUrl(urlTest)
//            loadUrl("file:///android_asset/WebDeDeTest.html")

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            // JavascriptInterface 설정 - name
            addJavascriptInterface(JavascriptInterface(viewModel), "Android")
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        }

        initInput()
        initResult()
    }

    private fun initResult() {
        val activityResultLauncher = registerForActivityResult(
            WebViewResultContract()
            /*ActivityResultContracts.StartActivityForResult()*/
        ) { uri ->
            Log.e(TAG, "ActivityResult uri [$uri]")

            // Shared Preferences 통해 Download Url 확인
            val url = context?.
            getSharedPreferences(WebViewSetting.SHARED_PREF_URL, Context.MODE_PRIVATE)?.
            getString(WebViewSetting.DOWNLOAD_URL_TITLE, "")
            Log.e(TAG, "ActivityResult Download_URL [$url]")
            url ?: return@registerForActivityResult

            Utils.startDownload(context = requireContext(), uri = uri.toUri(), downloadUrl = url)

        }
        binding.webView.setActivityResult(activityResultLauncher)
    }

    private fun initInput() {
        binding.apply {
            layoutInput.visibility = View.INVISIBLE
            ObjectAnimator.ofFloat(layoutInput, "translationY", 100f, 0f).apply {
                duration = 0
                start()
            }
            ibInputBack.setOnClickListener {
                hideKeyboard()
                showInputEdit(show = false)
            }
            ibInputSearch.setOnClickListener {
                hideKeyboard()
                showInputEdit(show = false)

                val input = edInput.text.toString()
                if (input.isEmpty()) return@setOnClickListener

                webView.loadUrl(input)
                Toast.makeText(context, input, Toast.LENGTH_SHORT).show()
            }
            edInput.isSingleLine = true
            fabExplore.visibility = View.VISIBLE
            fabExplore.setOnClickListener {
                showInputEdit()
            }
        }
    }

    private fun showInputEdit(show: Boolean = true) {
        binding.apply {
            if (show) {
                layoutInput.visibility = View.VISIBLE
//                fabExplore.visibility = View.INVISIBLE
                ObjectAnimator.ofFloat(fabExplore, "alpha", 1f, 0f)
                    .apply { start() }
                    .addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            fabExplore.visibility = View.INVISIBLE
                        }
                    })
                ObjectAnimator.ofFloat(layoutInput, "alpha", 0f, 1f).start()
                ObjectAnimator.ofFloat(layoutInput, "translationY", 100f, 0f).start()
            }
            else {
                fabExplore.visibility = View.VISIBLE
//                layoutInput.visibility = View.INVISIBLE
                ObjectAnimator.ofFloat(fabExplore, "alpha",0f, 1f).start()
                ObjectAnimator.ofFloat(layoutInput, "alpha",1f, 0f)
                    .apply { start() }
                    .addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            layoutInput.visibility = View.INVISIBLE
                            edInput.text.clear()
                        }
                    })
                ObjectAnimator.ofFloat(layoutInput, "translationY",100f).start()
            }
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let {
            (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .apply { hideSoftInputFromWindow(it.windowToken, 0) }
        }
    }

    override fun onBackPressed(): Boolean {
        if(webCanGoBack()) {
            webGoBack()
            return true
        }
        return super.onBackPressed()
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

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.e(TAG, "onPageFinished URL[$url]")

        // <html></html> 사이에 있는 html 소스를 넘겨준다.
        // onLoadResource 에서 저장한 image url 과 겹치는 부분이 많이 발생할 것으로 보임.
//        view?.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('img')[10].innerHTML);")
//        view?.loadUrl("javascript:(function(){" +
//                "var objs = document.getElementsByTagName(\"img\");" +
//                "for(var i=0;i<objs.length;i++)" +
//                "{" +
//                "window.Android.getImgSrc(objs[i].src);" +
//                "}" +
//                "})()")
        // TODO : Finished 기준으로 이미지 재정렬 동작
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?) {
        request ?: return
        val accept = request.requestHeaders["Accept"]
            Log.d(TAG, "shouldInterceptRequest ACCEPT[$accept] URL[${request.url}]")
        if(accept?.contains("image", ignoreCase = false) == true) {
            viewModel.addUrl(request.url.toString())
        }
    }
}