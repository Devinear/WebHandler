package com.shining.webhandler.view.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shining.webhandler.databinding.LayoutDashboardBinding
import com.shining.webhandler.view.common.base.BaseFragment
import com.shining.webhandler.view.collection.ItemListener
import android.text.Editable

import android.text.TextWatcher
import com.shining.webhandler.common.data.WebData

/**
 * DashboardFragment.kt
 * WebHandler
 */
class DashboardFragment : BaseFragment() {

    private lateinit var binding : LayoutDashboardBinding
    private val favoriteViewModel: DashboardViewModel by viewModels()
    private val recentViewModel: DashboardViewModel by viewModels()

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
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        initSearch()
        initFavorite()
        initRecent()
    }

    private fun initSearch() {
        binding.ibSearch.isEnabled = false
        binding.edtInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding.ibSearch.isEnabled = !s.isNullOrEmpty()
            }
        })
    }

    private fun initFavorite() {
        binding.reFavorite.apply {
            adapter = DashboardAdapter(
                listener = object : ItemListener<WebData> {
                    override fun clickImageItem(dataWeb: WebData, position: Int) {

                    }
                },
                viewModel = favoriteViewModel
            )

        }
    }

    private fun initRecent() {
        binding.reRecent.apply {
            adapter = DashboardAdapter(
                listener = object : ItemListener<WebData> {
                    override fun clickImageItem(dataWeb: WebData, position: Int) {

                    }
                },
                viewModel = recentViewModel
            )
        }
    }

    fun onClickSearch() {
        binding.edtInput
        Log.d(TAG, "onClickSearch")

    }
}