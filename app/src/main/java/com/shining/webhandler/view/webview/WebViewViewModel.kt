package com.shining.webhandler.view.webview

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shining.webhandler.common.ImageType
import com.shining.webhandler.util.GlideListener
import com.shining.webhandler.util.GlideManager
import com.shining.webhandler.view.base.BaseViewModel
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.common.data.ImageDataListener

/**
 * WebViewViewModel.kt
 * WebHandler
 */
//class WebViewViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return if (modelClass.isAssignableFrom(WebViewViewModel::class.java)) {
//            WebViewViewModel(context) as T
//        }
//        else {
//            throw IllegalAccessException()
//        }
//    }
//}

class WebViewViewModel(val context: Context) : BaseViewModel() {

    companion object {
        const val TAG = "[DE][VM] WebView"
    }

    /**
    * MutableLiveData : 값의 get/set 모두를 할 수 있다.
    * LiveData : 값의 get()만을 할 수 있다.
    * */

    private val imageUrls = HashSet<String>()

    private val _images = ObservableArrayList<ImageData>()
    val images : List<ImageData>
        get() = _images.toList()

    var listener : ImageDataListener? = null

    init {
        _images.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<ImageData>>() {
            override fun onItemRangeChanged(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
                Log.d(TAG, "Callback-onItemRangeChanged Start[$positionStart] Count[$itemCount]")
                listener?.onItemRangeChanged(sender, positionStart, itemCount)
            }

            override fun onChanged(sender: ObservableArrayList<ImageData>?) = Unit
            override fun onItemRangeInserted(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) = Unit
            override fun onItemRangeMoved(sender: ObservableArrayList<ImageData>?, fromPosition: Int, toPosition: Int, itemCount: Int) = Unit
            override fun onItemRangeRemoved(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int)  = Unit
        } )
    }

    fun addUrl(url: String) : Boolean {
        Log.d(TAG, "addUrl URL[$url]")

        // Returns - true if this set did not already contain the specified element
        if(imageUrls.add(url)) {
            for (type in ImageType.values()) {
                if (url.endsWith(type.toString(), ignoreCase = true)) {
                    request(url)
                    return true
                }
            }
            Log.d(TAG, "addUrl image Not Support Type")
        }
        else {
            Log.d(TAG, "addUrl image already contain")
        }
        return false
    }

    private fun request(url: String) {
        Log.d(TAG, "request URL[$url]")

        GlideManager.getBitmapFromUrl(context, url, object : GlideListener {
            override fun onSuccessResource(url: String, bitmap: Bitmap) {
                Log.d(TAG, "request-onSuccessResource URL[$url]")
                _images.add(ImageData(id = url.hashCode(), url = url, image = bitmap))
            }

            override fun onFailureResource(url: String) {
                Log.d(TAG, "request-onFailureResource URL[$url]")
            }
        })
    }

    fun clear() {
        Log.d(TAG, "clear")
        imageUrls.clear()
        _images.clear()
    }

    fun getImageSize() : Int = _images.size
}