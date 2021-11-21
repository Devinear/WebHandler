package com.shining.webhandler.view.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.databinding.LayoutCollectionBinding
import com.shining.webhandler.view.base.BaseFragment
import com.shining.webhandler.view.webview.WebViewViewModel

/**
 * CollectionFragment.kt
 * WebHandler
 */
class CollectionFragment : BaseFragment() {

    private lateinit var binding : LayoutCollectionBinding
    private val viewModel : WebViewViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T
                    = WebViewViewModel(requireActivity()) as T
        }
    }

    companion object {
        private const val TAG = "[DE][FR] Collection"
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CollectionFragment() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = LayoutCollectionBinding.inflate(layoutInflater, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        initRecycler()
    }

    private fun initRecycler() {
        binding.recycler.apply {
            adapter = CollectionAdapter(viewModel).apply {
                setHasStableIds(true)
                setHasFixedSize(true)
            }
            layoutManager = GridLayoutManager(requireActivity(), 3)

        }
    }

    override fun onBackPressed(): Boolean {
        if(binding.ivDetail.visibility == View.VISIBLE) {
            binding.ivDetail.visibility = View.GONE
            return true
        }
        return super.onBackPressed()
    }

    fun onClickTempAdd() {

    }
}