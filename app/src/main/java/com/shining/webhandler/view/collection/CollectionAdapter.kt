package com.shining.webhandler.view.collection

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shining.webhandler.common.data.ImageData
import com.shining.webhandler.common.data.ImageDataListener
import com.shining.webhandler.databinding.ItemGridImageBinding
import com.shining.webhandler.view.webview.WebViewViewModel

/**
 * CollectionAdapter.kt
 * WebHandler
 */
class CollectionAdapter(val vm: WebViewViewModel): ListAdapter<ImageData, CollectionAdapter.ViewHolder>(diffUtil) {

    companion object {
        const val TAG = "[DE] CollectionAdapter"

        val diffUtil = object: DiffUtil.ItemCallback<ImageData>() {
            override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData) =
                oldItem.id == newItem.id
        }
    }

    init {
        Log.d(TAG, "init")

        vm.listener = object : ImageDataListener {
            override fun onChanged(sender: List<ImageData>) {
                Log.d(TAG, "onChanged Size[${sender.size}]")
                submitList(sender)
            }
        }
    }

    class ViewHolder(val binding : ItemGridImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ImageData) {
            with(binding) {
                ivImage.setImageBitmap(data.image)
//                val rnd = Random()
//                ivImage.setBackgroundColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemGridImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder Position[$position]")
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }
}