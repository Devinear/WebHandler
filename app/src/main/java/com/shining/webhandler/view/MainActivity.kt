package com.shining.webhandler.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.shining.webhandler.R
import com.shining.webhandler.common.FragmentType
import com.shining.webhandler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var showFragment : FragmentType

    init {
        showFragment = FragmentType.WebView
    }

    companion object {
        const val TAG = "[DE][AC] Main"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        requestFragment()
    }

    private fun initUi() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // 왼쪽 상단 버튼
            setHomeAsUpIndicator(R.drawable.outline_menu_24) // 왼쪽 상단 버튼 아이콘
        }

        binding.drawerLayout.closeDrawers()
        binding.drawerNavigation.setNavigationItemSelectedListener {
            val id = it.itemId
            Log.d(TAG, "Drawer onNavigationItemSelected id[${id}]")
//            Toast.makeText(this@MainActivity, "Drawer Item ID : [$id]", Toast.LENGTH_SHORT).show()

            when(id) {
                R.id.dr_first -> { }
                R.id.dr_second -> { }
                R.id.dr_third -> { }
            }
            true
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            val id = it.itemId
            Log.d(TAG, "Bottom onNavigationItemSelected id[${id}]")

            when(id) {
                R.id.bo_forward -> {
                    if(showFragment != FragmentType.WebView)
                        requestFragment(FragmentType.WebView)
                    else
                        WebViewFragment.INSTANCE.webGoForward()
                }
                R.id.bo_back -> {
                    if(showFragment != FragmentType.WebView)
                        requestFragment(FragmentType.WebView)
                    else
                        WebViewFragment.INSTANCE.webGoBack()
                }
                R.id.bo_setting -> {
                    requestFragment(FragmentType.Setting)
                }
                else -> {
                    requestFragment(FragmentType.WebView)
                }
            }
            true
        }

        // HARDWARE ACCELERATED 필요..?
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 왼쪽 상단 버튼 눌렀을 때
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // 뒤로가기시 Drawer 먼저 처리
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun requestFragment(type: FragmentType = FragmentType.WebView) {
        showFragment = type

        when(type) {
            FragmentType.WebView -> {
                changeFragment(WebViewFragment.INSTANCE)
            }
            FragmentType.Setting -> {
                changeFragment(SettingFragment.INSTANCE)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // WebView BackKey
        if(keyCode == KeyEvent.KEYCODE_BACK && WebViewFragment.INSTANCE.webCanGoBack()) {
            WebViewFragment.INSTANCE.webGoBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun changeFragment(fragment: BaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment)
            .commit()
    }
}