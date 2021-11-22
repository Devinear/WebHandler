package com.shining.webhandler.view.collection

import android.annotation.SuppressLint
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
        with(binding) {
            laDetail/*ivDetail*/.apply {
                visibility = View.GONE
                setOnClickListener { visibility = View.GONE }
            }
            recycler.apply {
                adapter = CollectionAdapter(viewModel, object : ItemListener {
                    override fun clickImageItem(data: ImageData) {
                        laDetail.visibility = View.VISIBLE
//                        ivDetail.visibility = View.VISIBLE
                        ivDetail.setImageBitmap(data.image)

                        tvWidth.text = "W: ${data.image.width}"
                        tvHeight.text = "H : ${data.image.height}"
                    }
                }).apply {
                    setHasStableIds(true)
                    setHasFixedSize(true)
                }
                layoutManager = GridLayoutManager(requireActivity(), 3)
            }
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
        viewModel.checkedImageDownload(object : ProgressListener {
            override fun start(max: Int) {
                binding.apply {
                    laProgress.visibility = View.VISIBLE
                    progress.max = max
                }
            }

            @SuppressLint("SetTextI18n")
            override fun update(current: Int, max: Int, url: String) {
                Log.d(TAG, "ImageDownload_update Progress[$current] Max[$max]")
                binding.apply {
                    progress.progress = current
                    tvProgress.text = if(current != max ) "[ $current / $max ]" else "Complete!"
                    tvProgressUrl.text = url
                }
            }

            override fun complete() {
                binding.laProgress.visibility = View.GONE
            }
        })
    }
}