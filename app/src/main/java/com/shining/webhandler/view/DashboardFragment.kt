package com.shining.webhandler.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shining.webhandler.databinding.LayoutDashboardBinding

/**
 * DashboardFragment.kt
 * WebHandler
 */
class DashboardFragment : BaseFragment() {

    private lateinit var binding : LayoutDashboardBinding
    private lateinit var viewModel: DashboardViewModel

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DashboardFragment() }
        val TAG = "[DE][FR] Dashboard"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

    }

}