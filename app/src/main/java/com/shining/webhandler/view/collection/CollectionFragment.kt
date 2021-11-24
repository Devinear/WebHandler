package com.shining.webhandler.view.collection

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
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
        initProgress()
    }

    private fun initRecycler() {
        with(binding) {
            laDetail/*ivDetail*/.apply {
                visibility = View.GONE
                setOnClickListener { visibility = View.GONE }
            }
            recycler.apply {
                showCheckMode(show = false)

                adapter = CollectionAdapter(viewModel,
                    object : ItemListener {
                        override fun clickImageItem(data: ImageData) {
                            laDetail.visibility = View.VISIBLE
//                            ivDetail.visibility = View.VISIBLE
                            ivDetail.setImageBitmap(data.image)

                            tvWidth.text = "W: ${data.image.width}"
                            tvHeight.text = "H : ${data.image.height}"
                        } },
                    object : ItemLongListener {
                        override fun longClickImageItem(data: ImageData) {
                            binding.recycler.showCheckMode()
                            (binding.recycler.adapter as CollectionAdapter).setCheckMode()
                        }
                    }).apply {
                        setHasStableIds(true)
                        setHasFixedSize(true)
                    }
                layoutManager = GridLayoutManager(requireActivity(), 3)
            }
        }
    }

    private fun initProgress() {
        binding.laProgress.apply {
            visibility = View.GONE
            translationY = -100f // 최초 Show Animation을 위함
        }
    }

    override fun onBackPressed(): Boolean {
        // Check Mode 확인
        if((binding.recycler.adapter as CollectionAdapter).onBackPressed()) {
            binding.recycler.showCheckMode(show = false)
            return true
        }
        // Detail View 확인
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
                    showProgress(show = true)
                    progress.progress = 0
                    progress.max = max
                    tvProgress.text = "[ 0 / $max ]"
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
                showProgress(show = false)
            }
        })
    }

    private fun showProgress(show: Boolean = true) {
        binding.apply {
            if (show) {
                laProgress.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(laProgress, "alpha", 0f, 1f).start()
                ObjectAnimator.ofFloat(laProgress, "translationY", 0f).start()
            }
            else {
                ObjectAnimator.ofFloat(laProgress, "alpha",1f, 0f)
                    .apply { start() }
                    .addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            laProgress.visibility = View.GONE
                        }
                    })
                ObjectAnimator.ofFloat(laProgress, "translationY",-100f).start()
            }
        }
    }


}