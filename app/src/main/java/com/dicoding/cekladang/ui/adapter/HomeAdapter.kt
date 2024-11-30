package com.dicoding.cekladang.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cekladang.R

class HomeAdapter(
    private val homeList: List<String>,
    private val onItemClicked: (String) -> Unit,
) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_palawija, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = homeList[position]
        holder.textView.text = item
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
        Log.d("HomeAdapter", "Binding item: ${homeList[position]}")
    }

    override fun getItemCount(): Int = homeList.size

}