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
    ): View {
        binding = LayoutSettingBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        return view
    }

    // TODO : 추가하지 않을 이미지 크기 설정(Width, Height)
    // TODO : 최근 내역 삭제
    // TODO : 즐겨찾기 내역 삭제
}