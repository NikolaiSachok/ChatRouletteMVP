package com.video.chat.v.love.chatroulette.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.video.chat.v.love.chatroulette.databinding.EventsItemBinding
import com.video.chat.v.love.chatroulette.network.data.events.EventsData
import com.video.chat.v.love.chatroulette.utils.comparator.IntIdComparator

class EventsAdapter :
    ListAdapter<EventsData, RecyclerView.ViewHolder>(IntIdComparator<EventsData>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressesViewHolder(
            EventsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddressesViewHolder) {
            holder.bind(getItem(position))
        }
    }

    internal inner class AddressesViewHolder(private val binding: EventsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventsData) {
            with(binding) {
                tvTime.text = item.time
                tvName.text = item.name
                tvLocation.text = item.location
                tvTitledescription.text = item.titleDescription
                tvCode.text = item.code
                tvDescription.text = item.description
            }
        }
    }
}