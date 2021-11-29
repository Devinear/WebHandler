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

    private var isDownloadCancel = false
    val isCanceling : Boolean
        get() = isDownloadCancel

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
        val newUrl = if(url.indexOf('?') >= 0) url.substring(0, url.lastIndexOf('?')) else url
        Log.d(TAG, "addUrl NEW_URL[$newUrl]")

        // Returns - true if this set did not already contain the specified element
        if(imageUrls.add(newUrl)) {
//            request(url)
//            return true
            for (type in ImageType.values()) {
                if (newUrl.endsWith(type.toString(), ignoreCase = true)) {
                    request(newUrl, type)
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

    fun checkedImageDownload(listener: ProgressListener, name: String = "") {
        val list = _images.filter { it.checked }
        listener.start(max = list.size)
        viewModelScope.launch(Dispatchers.IO) {
            var progress = 0
            run {
                list.forEach {
                    val rename = if(name.isNotEmpty()) "${name}_${getNameCount(progress, _images.size)}" else ""
                    Utils.imageDownload(context = context, data = it, name = rename)
                    progress += 1

                    launch(Dispatchers.Main) {
                        listener.update(current = progress, max = list.size, url = it.url)
                        Log.d(TAG, "checkedImageDownload [$progress][${list.size}]")
                    }

                    if(isDownloadCancel) {
                        return@run
                    }
                }
            }
            launch(Dispatchers.Main) {
                delay(1000)
                isDownloadCancel = false
                listener.complete()
            }
        }
    }

    fun cancelDownload() {
        isDownloadCancel = true
    }

    private fun getNameCount(progress: Int, total: Int) : String {
        var p = progress
        var t = total

        var indexT = 0
        var indexP = 0
        while (t / 10 > 0) {
            t /= 10
            indexT += 1
        }
        while (p / 10 > 0) {
            p /= 10
            indexP += 1
        }
        var result = ""
        for(i in 1 .. indexT-indexP) {
            result = "0$result"
        }
        return "$result$progress"
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