package com.shining.webhandler.view.collection

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shining.webhandler.common.FlingType
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.databinding.LayoutCollectionBinding
import com.shining.webhandler.view.MainActivity
import com.shining.webhandler.view.base.BaseFragment
import com.shining.webhandler.view.webview.WebViewViewModel
import kotlin.math.abs

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

    private val liveCheckedCount = MutableLiveData<Int>()
    private var detailItemPosition = -1

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
        binding.vm = viewModel
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        initRecycler()
        initProgress()
        initDownload()
        initDetailView()
    }

    private fun initRecycler() {
        with(binding) {

            recycler.apply {
                adapter = CollectionAdapter(viewModel,
                    object : ItemListener {
                        override fun clickImageItem(data: ImageData, position: Int) {
                            showDetailView(show = true, data = data, position = position)
                        } },
                    object : ItemLongListener {
                        override fun longClickImageItem(data: ImageData) {
                            startCheckMode()
                            showDownload()
                        }
                    }, liveCheckedCount).apply {
                    setHasStableIds(true)
                    setHasFixedSize(true)
                }
                layoutManager = GridLayoutManager(requireActivity(), 3)
                startCheckMode(isCheckMode = false)
            }
        }
    }

    private fun initProgress() {
        binding.laProgress.apply {
            visibility = View.GONE
            translationY = -100f // 최초 Show Animation을 위함
        }
    }

    private fun initDownload() {
        liveCheckedCount.observe(viewLifecycleOwner, Observer { binding.tvCount.text = getCheckedCountText(it) })
        binding.laDownload.apply {
            visibility = View.GONE
            translationY = 100f // 최초 Show Animation을 위함
        }
        binding.tbAllNone.setOnCheckedChangeListener { buttonView, isChecked ->
            (binding.recycler.adapter as CollectionAdapter).checkItems(isChecked)
            binding.recycler.visibleItemsChecked(isChecked)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initDetailView() {

        val gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val type = when {
                    ((abs(velocityX) > abs(velocityY)) && velocityX > 0) -> FlingType.START
                    ((abs(velocityX) > abs(velocityY)) && velocityX < 0) -> FlingType.END
                    ((abs(velocityX) < abs(velocityY)) && velocityY > 0) -> FlingType.UP
                    else -> FlingType.DOWN
                }
                val position =
                    if (type == FlingType.START || type == FlingType.UP) detailItemPosition - 1
                    else detailItemPosition + 1
                try {
                    val data = (binding.recycler.adapter as CollectionAdapter).getItemData(position)
                    showDetailView(show = true, data = data, position = position)
                }
                catch (e: Exception) {
                    Log.e(TAG, "onFling DetailView Position[$position] Exception[${e.message}]")
                }
                finally {
                    Log.d(TAG, "onFling DetailView Type[$type]")
                }
                return true
            }
        })

        binding.apply {
            laDetail/*ivDetail*/.apply {
                visibility = View.GONE
                setOnClickListener { visibility = View.GONE }
            }
            laDetail.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }
    }

    fun showDetailView(show : Boolean = true, data: ImageData? = null, position: Int = -1) {
        Log.d(TAG, "showDetailView Show[$show]")
        detailItemPosition = if(show) position else -1

        binding.apply {
            laDetail.visibility = if(show) View.VISIBLE else View.GONE

            data?.run {
                ivDetail.setImageBitmap(data.image)
                tvPosition.text = "[ $position ]"
                tvWidth.text = "W: ${data.image.width}"
                tvHeight.text = "H : ${data.image.height}"
            }
        }

        (requireActivity() as MainActivity).enableViewPagerSwipe(enable = !show)
    }

    fun closeDetailView() = showDetailView(show = false)

    override fun onBackPressed(): Boolean {
        // Check Mode 확인
        if((binding.recycler.adapter as CollectionAdapter).onBackPressed()) {
            startCheckMode(isCheckMode = false)
            showDownload(show = false)
            return true
        }
        // Detail View 확인
        if(binding.laDetail.visibility == View.VISIBLE) {
            showDetailView(show = false)
            return true
        }
        return super.onBackPressed()
    }

    fun startCheckMode(isCheckMode: Boolean = true) {
        Log.d(TAG, "startCheckMode isCheckMode[$isCheckMode]")
        binding.recycler.apply {
            showCheckMode(show = isCheckMode)
            (adapter as CollectionAdapter).run {
                this.isCheckMode = isCheckMode
                if(!isCheckMode) {
                    checkItems(all = false)
                }
            }
        }
        pauseCheckMode(pause = false)
    }

    fun pauseCheckMode(pause: Boolean = true) {
        Log.d(TAG, "pauseCheckMode Pause[$pause]")
        (binding.recycler.adapter as CollectionAdapter).isPauseMode = pause
    }

    fun checkedItemsDownload() {
        hideKeyboard()
        pauseCheckMode()
        viewModel.checkedImageDownload(object : ProgressListener {
            override fun start(max: Int) {
                binding.apply {
                    showDownload(show = false)
                    showProgress(show = true)
                    progress.progress = 0
                    progress.max = max
                    tvProgress.text = "[ 0 / $max ]"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun update(current: Int, max: Int, url: String) {
                Log.d(TAG, "ImageDownload_update Progress[$current] Max[$max]")
                if(viewModel.isCanceling)
                    return

                binding.apply {
                    progress.progress = current
                    tvProgress.text = if(current != max ) "[ $current / $max ]" else "Complete!"
                    tvProgressUrl.text = url
                }
            }

            override fun complete() {
                showProgress(show = false)
                startCheckMode(isCheckMode = false)
            }
        }, name = binding.edtName.text.toString())
    }

    fun cancelDownload() {
        Log.d(TAG, "cancelDownload")
        binding.tvProgress.text = "Canceling..."
        viewModel.cancelDownload()
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

    private fun getCheckedCountText(count: Int, total: Int = binding.recycler.adapter?.itemCount?:0) : String = "[ $count / $total ]"

    private fun showDownload(show: Boolean = true) {
        binding.apply {
            if (show) {
                tvCount.text = getCheckedCountText(0)
                laDownload.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(laDownload, "alpha", 0f, 1f).start()
                ObjectAnimator.ofFloat(laDownload, "translationY", 0f).start()
            }
            else {
                ObjectAnimator.ofFloat(laDownload, "alpha",1f, 0f)
                    .apply { start() }
                    .addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            laDownload.visibility = View.GONE
                        }
                    })
                ObjectAnimator.ofFloat(laDownload, "translationY",100f).start()
            }
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let {
            (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .apply { hideSoftInputFromWindow(it.windowToken, 0) }
        }
    }
}