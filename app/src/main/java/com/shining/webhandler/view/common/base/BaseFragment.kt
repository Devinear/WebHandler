package com.shining.webhandler.view.common.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * BaseFragment.kt
 * WebHandler
 */
open class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    open fun initUi() { }

    open fun onBackPressed() : Boolean { return false }
}