package com.sdk.detectiveauidiobookapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sdk.detectiveauidiobookapp.data.model.Story
import com.sdk.detectiveauidiobookapp.databinding.SwipeItemBinding

class SwipeSongAdapter : RecyclerView.Adapter<SwipeSongAdapter.SongViewHolder>() {

    inner class SongViewHolder(val binding: SwipeItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    var songs: List<Story>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            SwipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.apply {
            val text = song.title
            tvPrimary.text = text
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }

    private var onItemClickListener: ((Story) -> Unit)? = null

    fun setOnItemClickListener(listener: (Story) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}