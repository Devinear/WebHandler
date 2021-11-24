package com.shining.webhandler.view.webview

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.shining.webhandler.common.ImageType
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.common.data.ImageDataListener
import com.shining.webhandler.util.GlideListener
import com.shining.webhandler.util.GlideManager
import com.shining.webhandler.util.Utils
import com.shining.webhandler.view.base.BaseViewModel
import com.shining.webhandler.view.collection.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
//                sender ?: return
//                listener?.onItemRangeChanged(sender, positionStart, itemCount)
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onChanged(sender: ObservableArrayList<ImageData>?) {
//                sender ?: return
//                listener?.onChanged(sender)
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeInserted(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
//                sender ?: return
//                listener?.onItemRangeInserted(sender, positionStart, itemCount)
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeMoved(sender: ObservableArrayList<ImageData>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
//                sender ?: return
//                listener?.onItemRangeMoved(sender, fromPosition, toPosition, itemCount)
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeRemoved(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
//                sender ?: return
//                listener?.onItemRangeRemoved(sender, positionStart, itemCount)
                sender?.run { listener?.onChanged(sender.toList()) }
            }
        } )
    }

    fun addUrl(url: String) : Boolean {
        Log.d(TAG, "addUrl URL[$url]")

        // Returns - true if this set did not already contain the specified element
        if(imageUrls.add(url)) {
            request(url)
            return true
            // 이미지 경로 중에는 확장자로 판단할 수 없는 경우가 많음..
//            for (type in ImageType.values()) {
//                if (url.endsWith(type.toString(), ignoreCase = true)) {
//                    request(url, type)
//                    return true
//                }
//            }
//            Log.d(TAG, "addUrl image Not Support Type")
        }
        else {
            Log.d(TAG, "addUrl image already contain")
        }
        return false
    }

    private fun request(url: String, type: ImageType = ImageType.NONE) {
        Log.d(TAG, "request URL[$url]")

        GlideManager.getBitmapFromUrl(context, url, object : GlideListener {
            override fun onSuccessResource(url: String, bitmap: Bitmap) {
                if((bitmap.width < 1000 && bitmap.height < 1000) && !bitmap.isRecycled) {
                    bitmap.recycle()
                    return
                }

                val width = 200
                val thumb = Bitmap.createScaledBitmap(bitmap, width, (bitmap.height*width)/(bitmap.width), true)
                Log.d(TAG, "request-onSuccessResource URL[$url]")
                _images.add(ImageData(id = url.hashCode().toUInt(), url = url, image = bitmap, thumb = thumb, type = type))
            }

            override fun onFailureResource(url: String) {
                Log.d(TAG, "request-onFailureResource URL[$url]")
            }
        })
    }

    fun checkedImageDownload(listener: ProgressListener) {
        val list = _images.filter { it.checked }
        listener.start(max = list.size)
        viewModelScope.launch(Dispatchers.IO) {
            var progress = 0
            list.forEach {
                Utils.imageDownload(context = context, data = it)
                progress += 1

                launch(Dispatchers.Main) {
                    listener.update(current = progress, max = list.size, url = it.url)
                    Log.d(TAG, "checkedImageDownload [$progress][${list.size}]")
                }
            }

            launch(Dispatchers.Main) {
                delay(1000)
                listener.complete()
            }
        }
    }

    fun clear() {
        Log.d(TAG, "clear")
        imageUrls.clear()
        _images.clear()
    }

    fun getImageSize() : Int = _images.size

    fun tempAdd() {
        val data = ImageData(id = (Math.random()*1000).toUInt(), image = _images[0].image, thumb = _images[0].thumb, type = _images[0].type)
        _images.add(data)
    }
}