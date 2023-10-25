package com.example.audioplayer.app.modules

import android.content.ContentResolver
import android.media.MediaPlayer
import com.example.audioplayer.data.PlayerManager
import com.example.audioplayer.data.StorageManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        StorageManager(
            contentResolver = get()
        )
    }

    single {
        PlayerManager(
            mediaPlayer = get(),
            storageManager = get(),
            context = androidContext()
        )
    }

    factory {
        MediaPlayer()
    }

    factory<ContentResolver> {
        androidContext().contentResolver
    }
}