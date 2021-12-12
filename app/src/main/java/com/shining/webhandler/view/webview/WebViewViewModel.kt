package com.shining.webhandler.view.webview

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.shining.webhandler.common.ImageType
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.common.listener.DataListener
import com.shining.webhandler.util.glide.GlideListener
import com.shining.webhandler.util.glide.GlideManager
import com.shining.webhandler.util.Utils
import com.shining.webhandler.view.common.base.BaseViewModel
import com.shining.webhandler.common.listener.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashSet

/**
 * WebViewViewModel.kt
 * WebHandler
 */
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

    var listener : DataListener<ImageData>? = null

    private var isDownloadCancel = false
    val isCanceling : Boolean
        get() = isDownloadCancel

    init {
        _images.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<ImageData>>() {
            override fun onItemRangeChanged(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onChanged(sender: ObservableArrayList<ImageData>?) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeInserted(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
                sender?.run {
                    listener?.onItemRangeInserted(sender, positionStart, itemCount)
                    listener?.onChanged(sender.toList())
                }
            }
            override fun onItemRangeMoved(sender: ObservableArrayList<ImageData>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeRemoved(sender: ObservableArrayList<ImageData>?, positionStart: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
        } )
    }

    fun addUrl(url: String) : Boolean {
//        Log.d(TAG, "addUrl URL[$url]")

        // 중복 검사
        if(!imageUrls.add(url)) {
            Log.d(TAG, "addUrl image already contain")
            return false
        }

        request(url)
        return true

//        if(imgTag) {
//            request(url)
//            return true
//        }
//
//        // URL 1차 검사
//        for (type in ImageType.values()) {
//            if (url.endsWith(type.toString(), ignoreCase = true)) {
//                request(url, type)
//                return true
//            }
//        }
//
//        // URL 2차 검사
//        if(url.indexOf('?') >= 0) {
//            val rightUrl = url.substring(0, url.lastIndexOf('?'))
//            for (type in ImageType.values()) {
//                if (rightUrl.endsWith(type.toString(), ignoreCase = true)) {
//                    request(rightUrl, type)
//                    return true
//                }
//            }
//        }
//        Log.d(TAG, "addUrl image Not Support Type")
//        return false
    }

    private fun request(url: String, type: ImageType = ImageType.NONE) {
        val data = ImageData(id = url.hashCode().toLong(), url = url, image = null, thumb = null, type = type)
        Log.d(TAG, "request ID[${data.id}] URL[$url]")
        _images.add(data)

        GlideManager.getBitmapFromUrl(context, url, object : GlideListener {
            override fun onSuccessResource(url: String, bitmap: Bitmap) {
                if(bitmap.width < 700 && bitmap.height < 700) {
                    if(!bitmap.isRecycled)
                        bitmap.recycle()
                    _images.remove(data)
                    Log.e(TAG, "request-remove ID[${data.id}] Size[${_images.size}]")
                    return
                }
                // ImageData가 삭제되는 경우 기존의 Position이 달라지기 때문에 Observe 해놓은 경우 잘못된 Position을 Update하는 경우가 발생한다.
                val position = _images.indexOf(data)
                Log.d(TAG, "request-success ID[${data.id}] URL[$url] Position[$position] Size[${_images.size}]")
                val width = 200
                val thumb =try {
                    Bitmap.createScaledBitmap(bitmap, width, (bitmap.height*width)/(bitmap.width), true)
                }
                catch (e: Exception) {
                    Log.e(TAG, "request-Exception:${e.message}")
                    null
                }

                data.thumb = thumb
                data.image = bitmap
                data.index.postValue(position)
            }

            override fun onFailureResource(url: String) {
                Log.e(TAG, "request-failure ID[${data.id}] URL[$url]")
                _images.remove(data)
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
                    val rename = if(name.isNotEmpty()) "${name}_${Utils.getNameCount(progress, _images.size)}" else ""
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

    fun clear() {
        Log.d(TAG, "clear")
        imageUrls.clear()
        _images.clear()
    }

    fun getImageSize() : Int = _images.size

}