package com.shining.webhandler.view.webview

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shining.webhandler.common.ImageType
import com.shining.webhandler.util.GlideListener
import com.shining.webhandler.util.GlideManager
import com.shining.webhandler.view.base.BaseViewModel

/**
 * WebViewViewModel.kt
 * WebHandler
 */
class WebViewViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WebViewViewModel::class.java)) {
            WebViewViewModel(context) as T
        }
        else {
            throw IllegalAccessException()
        }
    }
}

class WebViewViewModel(val context: Context) : BaseViewModel() {

    companion object {
        const val TAG = "[DE][VM] WebView"
    }

    /**
    * MutableLiveData : 값의 get/set 모두를 할 수 있다.
    * LiveData : 값의 get()만을 할 수 있다.
    * */

    private val imageUrls = HashSet<String>()
    private val images = ArrayList<Bitmap>()

    fun addUrl(url: String) : Boolean {
        Log.d(TAG, "addUrl URL[$url]")
        for (type in ImageType.values()) {
            if (url.endsWith(type.toString(), ignoreCase = true)) {
                imageUrls.add(url)

                GlideManager.getBitmapFromUrl(context, url, object : GlideListener {
                    override fun onSuccessResource(url: String, bitmap: Bitmap) {
                        images.add(bitmap)
                    }

                    override fun onFailureResource(url: String) {

                    }
                })

                Log.d(TAG, "addUrl SIZE[${imageUrls.size}]")
                return true
            }
        }
        Log.d(TAG, "addUrl Not Support Type")
        return false
    }
}