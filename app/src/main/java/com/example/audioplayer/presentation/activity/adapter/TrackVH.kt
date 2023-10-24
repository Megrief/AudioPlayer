package com.example.audioplayer.presentation.activity.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.databinding.TrackInListBinding
import com.example.audioplayer.entity.Track

class TrackVH(private val binding: TrackInListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            tracksName.text = track.trackName
            artistsName.text = track.artistName
        }
    }
}