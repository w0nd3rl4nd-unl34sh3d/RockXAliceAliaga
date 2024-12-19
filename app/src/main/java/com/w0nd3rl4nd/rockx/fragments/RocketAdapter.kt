package com.w0nd3rl4nd.rockx.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.w0nd3rl4nd.rockx.R
import com.w0nd3rl4nd.rockx.dbHandling.RocketEntity

class RocketAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<RocketEntity, RocketAdapter.RocketViewHolder>(RocketDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RocketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rocket, parent, false)
        return RocketViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RocketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RocketViewHolder(itemView: View, private val onItemClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val rocketNameTextView: TextView = itemView.findViewById(R.id.tvRocketName)

        fun bind(rocket: RocketEntity) {
            rocketNameTextView.text = rocket.name
            itemView.setOnClickListener { onItemClick(rocket.name) }
        }
    }

    class RocketDiffCallback : DiffUtil.ItemCallback<RocketEntity>() {
        override fun areItemsTheSame(oldItem: RocketEntity, newItem: RocketEntity): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: RocketEntity, newItem: RocketEntity): Boolean {
            return oldItem == newItem
        }
    }
}