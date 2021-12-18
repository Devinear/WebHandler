package com.shining.webhandler.view.setting

import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.shining.webhandler.App
import com.shining.webhandler.BuildConfig
import com.shining.webhandler.databinding.LayoutSettingBinding
import com.shining.webhandler.view.common.base.BaseFragment
import com.shining.webhandler.view.dashboard.FavoriteViewModel
import com.shining.webhandler.view.dashboard.RecentViewModel

/**
 * SettingFragment.kt
 * WebHandler
 */
class SettingFragment : BaseFragment<LayoutSettingBinding>(LayoutSettingBinding::inflate) {

    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val recentViewModel: RecentViewModel by activityViewModels()

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SettingFragment() }
        const val TAG = "$BASE Setting"
    }

    override fun initUi() {
        val minEnable = App.SHARED.minEnable

        binding.ckbSize.apply {
            isChecked = minEnable
            setOnCheckedChangeListener { _, isChecked ->
                App.SHARED.minEnable = isChecked
            }
        }

        binding.edtWidth.apply {
            isEnabled = minEnable
            setText(App.SHARED.minWidth.toString())
            addTextChangedListener {
                App.SHARED.minWidth = it.toString().toInt()
            }
        }

        binding.edtHeight.apply {
            isEnabled = minEnable
            setText(App.SHARED.minHeight.toString())
            addTextChangedListener {
                App.SHARED.minHeight = it.toString().toInt()
            }
        }
        /*
        binding.ckbPage.apply {
            isChecked = App.SHARED.onlyCurrent
            setOnCheckedChangeListener { _, isChecked ->
                App.SHARED.onlyCurrent = isChecked
            }
        }
        */
        /**
         * OnlyPage 아직 고려가 필요한 사항이 많다.
         * */
        binding.laPage.visibility = View.GONE
        App.SHARED.onlyCurrent = false

        binding.btFavorite.setOnClickListener {
            favoriteViewModel.removeAllData() {
                Toast.makeText(requireContext(), "Favorites Delete All!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btRecent.setOnClickListener {
            recentViewModel.removeAllData() {
                Toast.makeText(requireContext(), "Recent Delete All!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_NAME}"
    }
}