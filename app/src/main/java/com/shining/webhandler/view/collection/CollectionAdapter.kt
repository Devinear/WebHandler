package com.shining.webhandler.view.collection

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
class CollectionAdapter(val vm: WebViewViewModel, val listener: ItemListener, val longListener: ItemLongListener)
    : ListAdapter<ImageData, CollectionAdapter.ViewHolder>(diffUtil)
{

    companion object {
        const val TAG = "[DE] CollectionAdapter"

        val diffUtil = object: DiffUtil.ItemCallback<ImageData>() {
            override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData) =
                oldItem.id == newItem.id
        }
    }

    private var isCheckMode = false

    init {
        vm.listener = object : ImageDataListener {
            override fun onChanged(sender: List<ImageData>) =
                submitList(sender)
        }
    }

    class ViewHolder(val binding : ItemGridImageBinding,
                     val listener: ItemListener,
                     val longListener: ItemLongListener,
                     val adapter: CollectionAdapter)
        : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(data: ImageData, position: Int) {
//            Log.d(TAG, "bind Position[$position]")
            binding.apply {
                ivImage.setImageBitmap(data.thumb)
                ivImage.setOnClickListener {
                    if(!adapter.isCheckMode)
                        listener.clickImageItem(data)
                    else {
                        data.checked = !data.checked
                        ckbChecked.isChecked = data.checked
                    }
                }
                ivImage.setOnLongClickListener {
                    if(!adapter.isCheckMode) {
                        data.checked = true
                        longListener.longClickImageItem(data)
                    }
                    true
                }
                ckbChecked.isChecked = data.checked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemGridImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener,
            longListener,
            this
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    override fun getItemId(position: Int): Long =
        getItem(position).id.toLong()

    override fun getItemCount(): Int =
        currentList.size

    fun setCheckMode(checked: Boolean = true) {
        isCheckMode = true
    }

    fun onBackPressed() : Boolean {
        if(isCheckMode) {
            isCheckMode = false
            return true
        }
        return false
    }
}