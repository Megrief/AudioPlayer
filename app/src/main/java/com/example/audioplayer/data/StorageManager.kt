package com.example.audioplayer.data

import android.content.ContentResolver
import android.provider.MediaStore
import com.example.audioplayer.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StorageManager(
    private val contentResolver: ContentResolver
) {

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST
    )

    private val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    fun getTrackFlow(): Flow<Track?> = flow {
        contentResolver
            .query(mediaContentUri, projection, null, null, null)
            ?.use {  cursor ->
                val columnId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val columnTrack = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val columnArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                while (cursor.moveToNext()) {
                    val track = Track(
                        id = cursor.getLong(columnId),
                        trackName = cursor.getString(columnTrack),
                        artistName = cursor.getString(columnArtist)
                    )
                    emit(track)
                }
            } ?: emit(null)
    }

}