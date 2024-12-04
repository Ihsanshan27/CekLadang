package com.dicoding.cekladang.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<History, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onClick: ((History) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, onClick)
    }

    class MyViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: History, onClick: ((History) -> Unit)?) {
            binding.tvItemName.text = currentItem.name
            binding.tvItemDescription.text = currentItem.description
            binding.tvItemHasil.text = currentItem.prediction
            val dateFormat = SimpleDateFormat("dd MMM yyy, HH:mm", Locale.getDefault())
            val date = Date(currentItem.timestamp)
            binding.timestamp.text = dateFormat.format(date)
            binding.btnDelete.setOnClickListener {
                onClick?.invoke(currentItem)
            }
            Glide.with(binding.root)
                .load(currentItem.image)
                .into(binding.imgItemPhoto)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: History,
                newItem: History,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}