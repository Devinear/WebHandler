package com.shining.webhandler.repository.local.recent

import androidx.room.*

/**
 * WebRepoDao.kt
 * WebHandler
 */
@Dao
interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: RecentData)

    @Query("DELETE FROM WebRepository WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM WebRepository")
    suspend fun deleteAll()

    @Query("SELECT * FROM WebRepository")
    suspend fun getAll(): List<RecentData>
}