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
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.shining.webhandler.R
import com.shining.webhandler.common.FragmentType
import com.shining.webhandler.databinding.ActivityMainBinding
import com.shining.webhandler.view.common.base.BaseFragment
import com.shining.webhandler.view.collection.CollectionFragment
import com.shining.webhandler.view.common.PageAdapter
import com.shining.webhandler.view.common.base.BaseActivity
import com.shining.webhandler.view.dashboard.DashboardFragment
import com.shining.webhandler.view.setting.SettingFragment
import com.shining.webhandler.view.webview.WebViewFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private var showFragment : FragmentType
    init {
        showFragment = FragmentType.Dashboard
    }

    companion object {
        const val TAG = "$BASE Main"
    }

    override fun initUi() {
        initViewPager()
        initNavigationBar()
//        initBottomBar()

        // HARDWARE ACCELERATED 필요..?
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        requestFragment()
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
        }

        try {
            // ViewPager2 Swipe 민감도 조절
            val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            field.isAccessible = true

            val recyclerView = field[binding.viewPager] as RecyclerView
            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true

            val touchSlop = touchSlopField[recyclerView] as Int
            touchSlopField[recyclerView] = touchSlop * 6 //6 is empirical value
        }
        catch (e: Exception) {
            Log.e(TAG, "TouchSlop Exception [${e.message}]")
        }
    }

    fun enableViewPagerSwipe(enable: Boolean = true) {
        Log.d(TAG, "enableViewPagerSwipe Enable[$enable]")
        binding.viewPager.isUserInputEnabled = enable
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
    override fun onBackPressed() {
        if(!currentFragment().onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun currentFragment() : BaseFragment<*> =
        when (showFragment) {
            FragmentType.Dashboard -> DashboardFragment.INSTANCE
            FragmentType.WebView -> WebViewFragment.INSTANCE
            FragmentType.Collection -> CollectionFragment.INSTANCE
            FragmentType.Setting -> SettingFragment.INSTANCE
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

    fun requestWebLoad(url: String) {
        WebViewFragment.INSTANCE.requestWebLoad(url = url)
        requestFragment(FragmentType.WebView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeIf { showFragment == FragmentType.WebView }?.apply {
            WebViewFragment.INSTANCE.onWebViewResult(resultCode, requestCode, data)
        }
    }
}