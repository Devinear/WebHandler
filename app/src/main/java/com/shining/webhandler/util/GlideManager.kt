package com.shining.webhandler.util


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.constraintlayout.widget.Placeholder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * GlideManager.kt
 * WebHandler
 */
object GlideManager {

    fun getBitmapFromUrl(context: Context, url: String, listener: GlideListener) {
        GlideApp.with(context as Activity)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?)
                        = listener.onSuccessResource(url = url, bitmap = resource)
                override fun onLoadCleared(placeholder: Drawable?)
                        = listener.onFailureResource(url = url)
            })
    }

    fun setImageViewFromUrl(context: Context, url: String, view: ImageView, placeholder: Int, thumbnailSize: Float = 10f) {
        GlideApp.with(context as Activity)
            .load(url)
            .placeholder(placeholder)
            .thumbnail(thumbnailSize)
            .into(view)
    }

    fun setImageViewFromBitmap(context: Context, bitmap: Bitmap, view: ImageView, placeholder: Int, thumbnailSize: Float = 10f) {
        GlideApp.with(context as Activity)
            .load(bitmap)
            .placeholder(placeholder)
            .thumbnail(thumbnailSize)
            .into(view)
    }
}