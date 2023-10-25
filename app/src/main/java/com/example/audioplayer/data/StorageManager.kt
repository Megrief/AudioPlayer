package com.example.audioplayer.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
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

    private val selection = MediaStore.Audio.Media._ID + " =?"

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

    fun getById(id: Long): Flow<Track?> = flow {
        val selectionArgs = arrayOf(id.toString())
        contentResolver
            .query(mediaContentUri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                val columnId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val columnTrack = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val columnArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                if (cursor.moveToNext()) {
                        val track = Track(
                            id = cursor.getLong(columnId),
                            trackName = cursor.getString(columnTrack),
                            artistName = cursor.getString(columnArtist)
                        )
                        emit(track)
                } else emit(null)
            }
    }

    fun getUriById(id: Long): Flow<Uri?> = flow {
        val selectionArgs = arrayOf(id.toString())
        contentResolver
            .query(mediaContentUri, arrayOf(MediaStore.Audio.Media._ID), selection, selectionArgs, null)
            ?.use {  cursor ->
                if (cursor.moveToNext()) {
                    emit(ContentUris.withAppendedId(mediaContentUri, id))
                }
            } ?: emit(null)
    }

}