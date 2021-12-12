package com.shining.webhandler.repository.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * WebConverters.kt
 * WebHandler
 */
class WebConverters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        bitmap ?: return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        byteArray ?: return null
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}