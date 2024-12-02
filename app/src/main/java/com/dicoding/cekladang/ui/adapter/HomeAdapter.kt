package com.dicoding.cekladang.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cekladang.data.local.entity.Plants
import com.dicoding.cekladang.databinding.ItemPalawijaBinding

class HomeAdapter(
    private val plants: List<Plants>,
    private val onItemClicked: (Plants) -> Unit,
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: ItemPalawijaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plants) {
            binding.tvItemName.text = plant.name
            binding.root.setOnClickListener { onItemClicked(plant) } // Mengirimkan nama tanaman ke onItemClicked
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            ItemPalawijaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val plant = plants[position]
        holder.bind(plant) // Bind plant data ke ViewHolder
        Log.d("HomeAdapter", "Binding item: ${plant.name}")
    }

    override fun getItemCount(): Int = plants.size

}