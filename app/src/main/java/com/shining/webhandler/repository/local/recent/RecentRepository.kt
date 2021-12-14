package com.shining.webhandler.repository.local.recent

import com.shining.webhandler.App
import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.repository.local.BaseRepository
import com.shining.webhandler.repository.local.RepositoryListener
import com.shining.webhandler.repository.local.WebConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * WebLocalRepository.kt
 * WebHandler
 */
class RecentRepository : BaseRepository()  {

    private val dao = App.RECENT_DB.recentDao()

    companion object {
        val INSTANCE : RecentRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RecentRepository() }
    }

    override fun insert(data: WebData) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                RecentData(
                    id = data.id,
                    title = data.title,
                    url = data.url,
                    date = data.time,
                    icon = WebConverters().fromBitmap(data.icon)
                )
            )
        }
    }

    override fun remove(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(id)
        }
    }

    override fun getAll(listener: RepositoryListener) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = mutableListOf<WebData>()

            dao.getAll().forEach { repoData ->
                val data = WebData(
                    id = repoData.id,
                    title = repoData.title,
                    url = repoData.url,
                    time = repoData.date,
                    icon = WebConverters().toBitmap(repoData.icon)
                )
                list.add(data)
            }

            launch(Dispatchers.Main) {
                listener.requestAll(list)
            }
        }
    }
}