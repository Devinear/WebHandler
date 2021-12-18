package com.shining.webhandler.view.webview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shining.nwebview.NWebListener
import com.shining.nwebview.utils.WebViewSetting
import com.shining.webhandler.App
import com.shining.webhandler.R
import com.shining.webhandler.common.Constants
import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.databinding.LayoutWebviewBinding
import com.shining.webhandler.util.Utils
import com.shining.webhandler.view.common.base.BaseFragment
import com.shining.webhandler.view.dashboard.FavoriteViewModel
import com.shining.webhandler.view.dashboard.RecentViewModel
import java.net.URLEncoder
import java.util.*

/**
 * WebViewFragment.kt
 * WebHandler
 */
class WebViewFragment : BaseFragment<LayoutWebviewBinding>(LayoutWebviewBinding::inflate), NWebListener {

    // Kotlin 위임(by) 활용, 초기화되는 Activity 또는 Fragment Lifecycle 종속됨
    private val viewModel : WebViewViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T
                    = WebViewViewModel(requireActivity()) as T
        }
    }
    private var webData : WebData? = null
    private val favorite : FavoriteViewModel by activityViewModels()
    private val recent : RecentViewModel by activityViewModels()

    private var isUpdate = false
    private var requestUrl : String = ""

    var userAgent: String? = ""

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewFragment() }
        const val TAG = "$BASE WebView"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initUi() {
        Log.d(TAG, "initUi")
        binding.webView.isVisible = false

        binding.fragment = this@WebViewFragment
        binding.webView.settings.apply {
            builtInZoomControls = false                     // 화면 줌 동작
            displayZoomControls = false                     // 화면 줌 동작시, WebView 에서 줌 컨트롤 표시
            domStorageEnabled = true                        // 내부저장소
            allowFileAccess = true                          // File 엑세스
            allowContentAccess = true
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

            // Build.VERSION.SDK_INT >= 19
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            // JavascriptInterface 설정 - name
//            addJavascriptInterface(JavascriptInterface(viewModel), "Android")
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
            ibSearch.isEnabled = false
            ibFavorite.setImageDrawable(resources.getDrawable(R.drawable.outline_favorite_border_24))
            layoutInput.visibility = View.INVISIBLE
            layoutInput.translationY = 100f
            edtInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    binding.ibSearch.isEnabled = !s.isNullOrEmpty()
                }
            })
            fabExplore.visibility = View.VISIBLE
            fabExplore.setOnClickListener {
                showInputEdit()
            }
        }
        // TODO : 검색창 외부를 클릭시 창이 닫히도록
        // TODO : 검색창 UI 변경
    }

    private fun showInputEdit(show: Boolean = true) {
        binding.edtInput.setText(binding.webView.url ?: "")
        binding.ibSearch.isEnabled = !binding.edtInput.text.isNullOrEmpty()

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
                            edtInput.text?.clear()
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
            if(App.SHARED.onlyCurrent) {
                viewModel.clear()
            }
            webGoBack()
            return true
        }
        return super.onBackPressed()
    }

    override fun onResume() {
        Log.d(TAG, "onResume URL[$requestUrl]")
        super.onResume()

        if(requestUrl.isEmpty()) {
            showInputEdit()
        }

        binding.webView.apply {
            onResume()

            if(isUpdate) {
                executeWebLoad()
            }
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        hideKeyboard()
        showInputEdit(false)
        binding.webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        binding.webView.onDestroy()
        super.onDestroy()
    }

    fun requestWebLoad(url: String) {
        Log.d(TAG, "webLoad [$url]")
        isUpdate = true
        this.requestUrl = url
    }

    private fun executeWebLoad(url : String = requestUrl) {
        Log.d(TAG, "executeWebLoad [$url]")
        isUpdate = false
        requestUrl = url
        binding.webView.loadUrl(requestUrl)

        webData = recent.addWebData(data = WebData(id = requestUrl.hashCode().toLong(), url = requestUrl, time = Date().time))

        val favorite = favorite.isContain(id = webData!!.id)
        binding.ibFavorite.setImageDrawable(resources.getDrawable(if(favorite) R.drawable.outline_favorite_24 else R.drawable.outline_favorite_border_24))

    }

    fun onWebViewResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onWebViewResultForWebView")
//        super.onActivityResult(requestCode, resultCode, data)
    }

    fun webCanGoBack() : Boolean = binding.webView.canGoBack()

    fun webGoBack() = binding.webView.goBack()

    fun webGoForward() = binding.webView.goForward()

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) {
        Log.e(TAG, "shouldOverrideUrlLoading URL[${request?.url}]")

        if(App.SHARED.onlyCurrent) {
            viewModel.clear()
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.e(TAG, "onPageStarted URL[$url]")
        if(!binding.webView.isVisible)
            binding.webView.isVisible = true

        url ?: return
        webData = recent.addWebData(data = WebData(id = url.hashCode().toLong(), url = url, time = Date().time, icon = favicon))

        val favorite = favorite.isContain(id = webData!!.id)
        binding.ibFavorite.setImageDrawable(resources.getDrawable(if(favorite) R.drawable.outline_favorite_24 else R.drawable.outline_favorite_border_24))
    }

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

        binding.edtInput.setText( url ?: "")
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?) {
        request ?: return
        val accept = request.requestHeaders["Accept"]
//        Log.d(TAG, "shouldInterceptRequest ACCEPT[$accept] URL[${request.url}]")

        if(accept?.contains("image", ignoreCase = false) == true) {
            viewModel.addUrl(request.url.toString())
        }
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap) {
        Log.d(TAG, "onReceivedIcon")
        webData?.icon = icon
        recent.isCompleted(data = webData)
    }

    override fun onReceivedTitle(view: WebView, title: String) {
        Log.d(TAG, " Title[$title]")
        webData?.title = title
        recent.isCompleted(data = webData)
    }

    fun onClickBack() {
        hideKeyboard()
        showInputEdit(show = false)
    }

    fun onClickSearch() {
        var search = binding.edtInput.text.toString()
        if (search.isEmpty()) return

        if(!search.startsWith("http")) {
            search = "${Constants.GOOGLE_WEB}${URLEncoder.encode(search, "UTF-8")}"
        }
        executeWebLoad(search)

        binding.edtInput.text?.clear()
        binding.edtInput.clearFocus()

        hideKeyboard()
        showInputEdit(show = false)
    }

    fun onClickFavorite() {
        webData ?: return

        val isFavorite = favorite.isContain(id = webData!!.id)
        binding.ibFavorite.setImageDrawable(resources.getDrawable(if(isFavorite) R.drawable.outline_favorite_border_24 else R.drawable.outline_favorite_24))

        if(!isFavorite) {
            favorite.addWebData(webData!!)
        }
    }
}