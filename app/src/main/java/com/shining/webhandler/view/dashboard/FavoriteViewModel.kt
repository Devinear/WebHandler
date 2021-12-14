package com.shining.webhandler.view.dashboard

import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.repository.local.RepositoryListener
import com.shining.webhandler.repository.local.favorite.FavoriteRepository

/**
 * FavoriteViewModel.kt
 * WebHandler
 */
class FavoriteViewModel : DashboardViewModel() {

    init {
        repository = FavoriteRepository.INSTANCE
        requestDatabase()
    }

    override fun addWebData(data: WebData): WebData {
        val data = super.addWebData(data)
        repository.insert(data = data)
        return data
    }
}