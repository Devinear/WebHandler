package com.shining.webhandler.repository.local

import com.shining.webhandler.App
import com.shining.webhandler.common.data.WebData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * WebLocalRepository.kt
 * WebHandler
 */
class WebLocalRepository {

    private val dao = App.WEB_DATABASE.webRepoDao()

    companion object {
        val INSTANCE : WebLocalRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebLocalRepository() }
    }

    fun insert(data: WebData) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                WebRepoData(
                    id = data.id,
                    title = data.title,
                    url = data.url,
                    date = data.time,
                    icon = WebConverters().fromBitmap(data.icon)
                )
            )
        }
    }

    fun remove(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(id)
        }
    }

    fun getAll(listener: RepositoryListener) {
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