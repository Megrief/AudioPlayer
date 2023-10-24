package com.example.audioplayer.presentation.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.databinding.TrackInListBinding
import com.example.audioplayer.entity.Track

class TrackAdapter(private val onClickListener: (Track) -> Unit) : RecyclerView.Adapter<TrackVH>() {
    private val trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVH {
        val binding = TrackInListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackVH(binding)
    }

    override fun getItemCount(): Int = trackList.size

    override fun onBindViewHolder(holder: TrackVH, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onClickListener(track)
        }
    }

    fun setContent(newList: List<Track>) {
        trackList.clear()
        trackList.addAll(newList)
        notifyDataSetChanged()
    }
}