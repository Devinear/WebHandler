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
class CollectionAdapter(val vm: WebViewViewModel, val listener: ItemListener): ListAdapter<ImageData, CollectionAdapter.ViewHolder>(diffUtil) {

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
        vm.listener = object : ImageDataListener {
            override fun onChanged(sender: List<ImageData>) =
                submitList(sender)
        }
    }

    class ViewHolder(val binding : ItemGridImageBinding, val listener: ItemListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ImageData, position: Int) {
            Log.d(TAG, "bind Position[$position] Checked[${data.checked}]")
            binding.apply {
                ivImage.setImageBitmap(data.thumb)
                ivImage.setOnClickListener {
                    listener.clickImageItem(data)
                }
                ckbChecked.isChecked = data.checked
                ckbChecked.setOnClickListener {
                    data.checked = !data.checked
                }
                // onCheckedChange 실제 동작이 아닌 뷰가 재사용될때에도 호출되어 기존 체크가 해제
//                ckbChecked.setOnCheckedChangeListener { _, isChecked ->
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemGridImageBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    override fun getItemId(position: Int): Long =
        getItem(position).id.toLong()

    override fun getItemCount(): Int =
        currentList.size
}