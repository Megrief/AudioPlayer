package com.example.audioplayer.app.modules

import android.content.ContentResolver
import com.example.audioplayer.data.StorageManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        StorageManager(
            contentResolver = get()
        )
    }

    factory<ContentResolver> {
        androidContext().contentResolver
    }
}