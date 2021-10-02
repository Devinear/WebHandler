package com.shining.webhandler.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.shining.webhandler.R

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

//    private var webView : WebView? = null
//    private val github = "https://github.com/"
    private val github = "https://www.naver.com/"

    private val frWebView by lazy { WebViewFragment() }
    private val frSetting by lazy { SettingFragment() }

    companion object {
        const val TAG = "[DE][AC] Main"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        webView = findViewById(R.id.webView)
//        webView?.apply {
//            settings.javaScriptEnabled = true
//            loadUrl(github)
//            webChromeClient = WebChromeClientClass()
//            webViewClient = WebViewClientClass()
//
//            // Build.VERSION.SDK_INT >= 19
//            setLayerType(View.LAYER_TYPE_HARDWARE, null)
//        }
//        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setOnItemSelectedListener(this)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        // WebView BackKey
//        if(keyCode == KeyEvent.KEYCODE_BACK && webView?.canGoBack() == true) {
//            webView?.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.d(TAG, "onNavigationItemSelected id[${id}]")

        when(id) {
            R.id.setting -> {
                changeFragment(frSetting)
            }
            else -> {
                changeFragment(frWebView)
            }
        }

        return true
    }

    private fun changeFragment(fragment: BaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment)
            .commit()
    }
}