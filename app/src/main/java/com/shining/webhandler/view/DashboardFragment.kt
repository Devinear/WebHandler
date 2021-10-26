package com.shining.webhandler.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import com.shining.webhandler.databinding.LayoutDashboardBinding

import com.shining.webhandler.databinding.ViewMarkerBinding
import com.shining.webhandler.view.base.BaseFragment

/**
 * DashboardFragment.kt
 * WebHandler
 */
class DashboardFragment : BaseFragment() {

    private lateinit var binding : LayoutDashboardBinding
    private lateinit var bindingMarker : ViewMarkerBinding
    private lateinit var viewModel: DashboardViewModel

    companion object {
        private const val TAG = "[DE][FR] Dashboard"
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DashboardFragment() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDashboardBinding.inflate(layoutInflater, container, false)
        bindingMarker = ViewMarkerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

    }

    private fun entryIntValue(x: Int, y: Int) : Entry = Entry(x.toFloat(), y.toFloat())
}