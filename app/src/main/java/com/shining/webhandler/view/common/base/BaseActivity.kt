package com.shining.webhandler.view.common.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.shining.webhandler.common.Constants

/**
 * BaseActivity.kt
 * WebHandler
 */
abstract class BaseActivity<T : ViewBinding>(
    private val inflate : (LayoutInflater) -> T
) : AppCompatActivity() {

    lateinit var binding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    abstract fun initUi()


    companion object {
        const val BASE = "${Constants.TAG}[AC]"
    }

}