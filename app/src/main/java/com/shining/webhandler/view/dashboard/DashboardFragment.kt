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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shining.webhandler.common.Constants
import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.view.MainActivity
import com.shining.webhandler.view.collection.ItemSizeListener
import java.net.URLEncoder

/**
 * DashboardFragment.kt
 * WebHandler
 */
class DashboardFragment : BaseFragment() {

    private lateinit var binding : LayoutDashboardBinding
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val recentViewModel: RecentViewModel by activityViewModels()

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
        binding.fragment = this@DashboardFragment

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
        binding.tvFeEmpty.visibility = if(favoriteViewModel.webs.isEmpty()) View.VISIBLE else View.GONE
        binding.reFavorite.apply {
            adapter = DashboardAdapter(
                listener = object : ItemListener<WebData> {
                    override fun clickImageItem(webData: WebData, position: Int) {
                        (activity as MainActivity).requestWebLoad(url = webData.url)
                    }
                },
                sizeListener = object : ItemSizeListener {
                    override fun changedSize(size: Int) {
                        binding.tvFeEmpty.visibility = if(size == 0) View.VISIBLE else View.GONE
                    }
                },
                viewModel = favoriteViewModel
            ).apply {
                setHasStableIds(true)
                setHasFixedSize(true)
            }
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initRecent() {
        binding.tvReEmpty.visibility = if(recentViewModel.webs.isEmpty()) View.VISIBLE else View.GONE
        binding.reRecent.apply {
            adapter = DashboardAdapter(
                listener = object : ItemListener<WebData> {
                    override fun clickImageItem(webData: WebData, position: Int) {
                        (activity as MainActivity).requestWebLoad(url = webData.url)
                    }
                },
                sizeListener = object : ItemSizeListener {
                    override fun changedSize(size: Int) {
                        binding.tvReEmpty.visibility = if(size == 0) View.VISIBLE else View.GONE
                    }
                },
                viewModel = recentViewModel
            ).apply {
                setHasStableIds(true)
                setHasFixedSize(true)
            }
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun onClickSearch() {
        var search = binding.edtInput.text.toString()
        if(!search.startsWith("http")) {
            search = "${Constants.GOOGLE_WEB}${URLEncoder.encode(search, "UTF-8")}"
        }
        Log.d(TAG, "onClickSearch [$search]")
        binding.edtInput.text?.clear()
        binding.edtInput.clearFocus()

        (activity as MainActivity).requestWebLoad(url = search)
    }

    override fun onResume() {
        super.onResume()
        (binding.reFavorite.adapter as DashboardAdapter).submitList(favoriteViewModel.webs)
        (binding.reRecent.adapter as DashboardAdapter).submitList(recentViewModel.webs)
        Log.d(TAG, "onResume")
    }
}