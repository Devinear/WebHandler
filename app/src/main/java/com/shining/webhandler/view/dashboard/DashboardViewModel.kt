package com.shining.webhandler.view.dashboard

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.common.listener.DataListener
import com.shining.webhandler.view.common.base.BaseViewModel

/**
 * DashboardViewModel.kt
 * WebHandler
 */
open class DashboardViewModel : BaseViewModel() {

    companion object {
        const val TAG = "[DE][VM] Dashboard"
    }

    private val _webs = ObservableArrayList<WebData>()
    val webs : List<WebData>
        get() = _webs.toList()

    var listener : DataListener<WebData>? = null

    init {
        _webs.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<WebData>>() {
            override fun onItemRangeChanged(sender: ObservableArrayList<WebData>?, positionStart: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onChanged(sender: ObservableArrayList<WebData>?) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeInserted(sender: ObservableArrayList<WebData>?, positionStart: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeMoved(sender: ObservableArrayList<WebData>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
            override fun onItemRangeRemoved(sender: ObservableArrayList<WebData>?, positionStart: Int, itemCount: Int) {
                sender?.run { listener?.onChanged(sender.toList()) }
            }
        } )
    }

    fun addWebData(data: WebData) : WebData {
        Log.d(TAG, "addWebData ID[${data.id}]")
        _webs.forEach { webData ->
            if(webData.id == data.id)
                return webData
        }
        _webs.add(data)
        return data
    }

    fun isContain(id: UInt) : Boolean {
        _webs.forEach { webData ->
            if(webData.id == id) return true
        }
        return false
    }

    fun removeWebData(data: WebData) : Boolean {
        return _webs.remove(data)
    }
}