package com.example.newpipeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newpipeapp.databinding.ItemVideoBinding
import org.schabi.newpipe.extractor.search.SearchInfoItem

class VideoAdapter(val onClick: (videoId: String) -> Unit) : ListAdapter<SearchInfoItem.VideoSearchResult, VideoAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchInfoItem.VideoSearchResult>() {
            override fun areItemsTheSame(oldItem: SearchInfoItem.VideoSearchResult, newItem: SearchInfoItem.VideoSearchResult) =
                oldItem.videoId == newItem.videoId

            override fun areContentsTheSame(oldItem: SearchInfoItem.VideoSearchResult, newItem: SearchInfoItem.VideoSearchResult) =
                oldItem == newItem
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchInfoItem.VideoSearchResult) {
            binding.titleTextView.text = item.title
            Glide.with(binding.thumbnailImageView.context)
                .load(item.thumbnailUrl)
                .centerCrop()
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener { onClick(item.videoId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

