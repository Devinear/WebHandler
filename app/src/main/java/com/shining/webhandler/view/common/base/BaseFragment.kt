package com.shining.webhandler.view.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.shining.webhandler.common.Constants

/**
 * BaseFragment.kt
 * shopby-admin-app-android-starter
 */
abstract class BaseFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    companion object {
        const val BASE = "${Constants.TAG}[FR]"
    }

    private var _binding : T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    abstract fun initUi()

    open fun onBackPressed() : Boolean { return false }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}