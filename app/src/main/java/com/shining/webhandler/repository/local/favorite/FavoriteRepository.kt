package com.shining.webhandler.repository.local.favorite

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
class FavoriteRepository : BaseRepository() {

    private val dao = App.FAVORITE_DB.favoriteDao()

    companion object {
        val INSTANCE : FavoriteRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FavoriteRepository() }
    }

    override fun insert(data: WebData) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                FavoriteData(
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

    override fun removeAll(listener: RepositoryListener) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAll()

            launch(Dispatchers.Main) {
                listener.removeAll()
            }
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