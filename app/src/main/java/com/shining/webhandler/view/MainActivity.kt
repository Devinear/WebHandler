package com.shining.webhandler.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.shining.webhandler.R
import com.shining.webhandler.common.FragmentType
import com.shining.webhandler.databinding.ActivityMainBinding
import com.shining.webhandler.view.collection.CollectionFragment
import com.shining.webhandler.view.common.PageAdapter
import com.shining.webhandler.view.dashboard.DashboardFragment
import com.shining.webhandler.view.setting.SettingFragment
import com.shining.webhandler.view.webview.WebViewFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var showFragment : FragmentType

    init {
        showFragment = FragmentType.Dashboard
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
        initHeaderUi()
        initViewPager()
        initDrawerUi()
        initNavigationBar()
//        initBottomBar()

        // HARDWARE ACCELERATED 필요..?
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    private fun initHeaderUi() {
        setSupportActionBar(binding.toolbar)

        // Toolbar 비활성화
        binding.toolbar.visibility = View.GONE

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // 왼쪽 상단 버튼
            setHomeAsUpIndicator(R.drawable.outline_menu_24) // 왼쪽 상단 버튼 아이콘
        }
    }

    private fun initViewPager() {
        binding.viewPager.apply {
            adapter = PageAdapter(this@MainActivity,
                arrayOf(DashboardFragment.INSTANCE, WebViewFragment.INSTANCE, CollectionFragment.INSTANCE, SettingFragment.INSTANCE).toList())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val navigation = when (position) {
                        0 -> R.id.bo_dashboard
                        1 -> R.id.bo_web
                        2 -> R.id.bo_collections
                        else -> R.id.bo_setting // 3
                    }
                    if(binding.bottomNavigation.selectedItemId != navigation)
                        binding.bottomNavigation.selectedItemId = navigation
                }
            })

            isUserInputEnabled = false // 좌우 스와이프 동작
        }
    }

    private fun initDrawerUi() {
        // Drawer 비활성화
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayout.closeDrawers()
        binding.drawerNavigation.setNavigationItemSelectedListener {
            val id = it.itemId
            when(id) {
                R.id.dr_first -> { }
                R.id.dr_second -> { }
                R.id.dr_third -> { }
            }
            true
        }
    }

    private fun initNavigationBar() {
        binding.bottomNavigation.setOnItemSelectedListener {
            val id = it.itemId
            Log.d(TAG, "Bottom onNavigationItemSelected id[${id}]")

            when(id) {
                R.id.bo_dashboard ->    requestFragment(FragmentType.Dashboard)
                R.id.bo_web ->          requestFragment(FragmentType.WebView)
                R.id.bo_collections ->  requestFragment(FragmentType.Collection)
                R.id.bo_setting ->      requestFragment(FragmentType.Setting)
            }
            true
        }
    }

/*
    private fun initBottomBar() {
        val items = listOf(
            BItem(this, text = "1", iconNormal = R.drawable.outline_collections_24,
                iconSelected = R.mipmap.ic_launcher, badgeNumber = 8),
            BItem(this, text = "2", iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher, badgeNumber = 22),
            BItem(this, iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher, badgeNumber = 888),
            BItem(this, text = "3", iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher, isShowPoint = true),
            BItem(this, text = "4", iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher),
            BItem(this, text = "5", iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher),
            BItem(this, text = "6", iconNormal = R.mipmap.ic_launcher,
                iconSelected = R.mipmap.ic_launcher)
        )
        binding.bottomBar.apply {
            addItem(*(items.toTypedArray()))
            selectState(0)
            setOnSelectedListener { prePosition, position -> }
            setOnReSelectedListener { position -> }
            setOnPressedListener { position ->
                Toast.makeText(this@MainActivity, "PRESSED INDEX [$position]", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

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

    private fun requestFragment(type: FragmentType = FragmentType.Dashboard) {
        showFragment = type
        val page = when(type) {
            FragmentType.Dashboard -> 0
            FragmentType.WebView -> 1
            FragmentType.Collection -> 2
            else -> 3
        }
        if(binding.viewPager.currentItem != page) {
            binding.viewPager.currentItem = page
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // WebView BackKey
        if(keyCode == KeyEvent.KEYCODE_BACK &&
            showFragment == FragmentType.WebView && WebViewFragment.INSTANCE.webCanGoBack()) {
            WebViewFragment.INSTANCE.webGoBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeIf { showFragment == FragmentType.WebView }?.apply {
            WebViewFragment.INSTANCE.onWebViewResult(resultCode, requestCode, data)
        }
    }
}