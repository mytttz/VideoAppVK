package com.example.videoappvk.videolist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.videoappvk.R
import com.example.videoappvk.dataclasses.Video
import java.util.Locale


class VideoAdapter(
    private val selectedVideo: (Video) -> Unit
) :
    PagingDataAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView = itemView.findViewById(R.id.video_thumbnail)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val videoDuration: TextView = itemView.findViewById(R.id.video_duration)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val video = getItem(position)
                    if (video != null) {
                        selectedVideo(video)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.video_list_item, parent, false)
        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        video?.let {
            holder.userName.text = it.user.name
            holder.videoDuration.text = formatDuration(it.duration.toInt())
            Glide.with(holder.itemView.context)
                .load(it.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.stub_icon)
                .error(R.drawable.stub_icon)
                .override(
                    Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL
                )
                .into(holder.videoThumbnail)
        }
    }


    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(
            oldItem: Video,
            newItem: Video
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Video,
            newItem: Video
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.US, "%d:%02d", minutes, remainingSeconds)
    }
}

