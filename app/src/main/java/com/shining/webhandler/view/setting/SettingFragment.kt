package com.shining.webhandler.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shining.webhandler.databinding.LayoutSettingBinding
import com.shining.webhandler.view.common.base.BaseFragment

/**
 * SettingFragment.kt
 * WebHandler
 */
class SettingFragment : BaseFragment() {

    private lateinit var binding : LayoutSettingBinding

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SettingFragment() }
        val TAG = "[DE][FR] Setting"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSettingBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        return view
    }
}