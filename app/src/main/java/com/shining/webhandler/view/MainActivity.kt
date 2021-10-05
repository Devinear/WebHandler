package com.shining.webhandler.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.shining.webhandler.R
import com.shining.webhandler.common.FragmentType
import com.shining.webhandler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    // ViewBinding
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
        val view = binding.root
        setContentView(view)

        initUi()
        requestFragment()
    }

    private fun initUi() {
        binding.bottomNavigationView.setOnItemSelectedListener(this@MainActivity)

        // HARDWARE ACCELERATED 필요..?
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.d(TAG, "onNavigationItemSelected id[${id}]")

        when(id) {
            R.id.setting -> {
                requestFragment(FragmentType.Setting)
            }
            else -> {
                requestFragment(FragmentType.WebView)
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