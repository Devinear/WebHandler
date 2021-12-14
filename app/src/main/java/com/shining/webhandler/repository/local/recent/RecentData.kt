package com.shining.webhandler.repository.local.recent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WebRepoData.kt
 * WebHandler
 */
@Entity(tableName = "WebRepository")
data class RecentData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val icon: ByteArray? = null
)