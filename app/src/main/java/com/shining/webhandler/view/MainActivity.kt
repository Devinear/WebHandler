package com.shining.webhandler.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.shining.webhandler.R

class MainActivity : AppCompatActivity(R.layout.activity_main), NavigationBarView.OnItemSelectedListener {

    companion object {
        const val TAG = "[DE][AC] Main"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()
        requestFragment()
    }

    private fun initUi() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setOnItemSelectedListener(this)
    }

    fun requestFragment() {
        changeFragment(WebViewFragment.INSTANCE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.d(TAG, "onNavigationItemSelected id[${id}]")

        when(id) {
            R.id.setting -> {
                changeFragment(SettingFragment.INSTANCE)
            }
            else -> {
                changeFragment(WebViewFragment.INSTANCE)
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