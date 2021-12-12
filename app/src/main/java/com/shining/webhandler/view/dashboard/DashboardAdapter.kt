package com.shining.webhandler.view.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shining.webhandler.common.data.WebData
import com.shining.webhandler.common.listener.DataListener
import com.shining.webhandler.databinding.LayoutDashboardItemBinding
import com.shining.webhandler.view.collection.ItemListener
import com.shining.webhandler.view.collection.ItemSizeListener

/**
 * DashboardAdapter.kt
 * WebHandler
 */
class DashboardAdapter(val listener: ItemListener<WebData>, val sizeListener: ItemSizeListener, val viewModel: DashboardViewModel)
    : ListAdapter<WebData, DashboardAdapter.ViewHolder>(diffUtil)
{

    companion object {
        const val TAG = "[DE] DashboardAdapter"

        val diffUtil = object: DiffUtil.ItemCallback<WebData>() {
            override fun areContentsTheSame(oldItem: WebData, newItem: WebData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: WebData, newItem: WebData) =
                oldItem.id == newItem.id
        }
    }

    init {
        viewModel.listener = object : DataListener<WebData> {
            override fun onChanged(sender: List<WebData>) {
                sizeListener.changedSize(sender.size)
                submitList(sender)
            }
        }
    }

    class ViewHolder(val binding : LayoutDashboardItemBinding,
                     val listener: ItemListener<WebData>
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(web: WebData, position: Int) {
            binding.apply {
                web.icon?.also { bitmap ->
                    ivIcon.setImageBitmap(bitmap)
                }
                tvTitle.text = web.title
                tvUrl.text = web.url
            }
        }
    }

    override fun getItemId(position: Int): Long = getItem(position)?.id?.toLong() ?: -1L

    override fun getItemCount(): Int = currentList.size

    fun getItemData(position: Int) : WebData = getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutDashboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}