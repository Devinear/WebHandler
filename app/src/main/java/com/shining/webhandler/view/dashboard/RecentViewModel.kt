package com.shining.webhandler.view.dashboard

import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.repository.local.RepositoryListener
import com.shining.webhandler.repository.local.recent.RecentRepository

/**
 * RecentViewModel.kt
 * WebHandler
 */
class RecentViewModel : DashboardViewModel() {

    init {
        repository = RecentRepository.INSTANCE
        requestDatabase()
    }

    override fun addWebData(data: WebData): WebData {
        val data = super.addWebData(data)
        return data
    }

    fun isCompleted(data: WebData?) {
        data ?: return

        if(data.icon != null && data.title.isNotEmpty())
            repository.insert(data)
    }
}