package com.shining.webhandler.view.dashboard

import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.repository.local.RepositoryListener
import com.shining.webhandler.repository.local.WebLocalRepository

/**
 * FavoriteViewModel.kt
 * WebHandler
 */
class FavoriteViewModel : DashboardViewModel() {
    private val repository = WebLocalRepository.INSTANCE

    init {
        repository.getAll(object : RepositoryListener {
            override fun requestAll(list: List<WebData>) {
                list.forEach { data ->
                    addWebData(data)
                }
            }
        })
    }

    override fun addWebData(data: WebData): WebData {
        val data = super.addWebData(data)
        repository.insert(data)
        return data
    }

    override fun removeWebData(data: WebData): Boolean {
        repository.remove(data.id)
        return super.removeWebData(data)
    }
}