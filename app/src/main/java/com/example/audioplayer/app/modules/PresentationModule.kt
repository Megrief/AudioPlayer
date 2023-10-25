package com.example.audioplayer.app.modules

import com.example.audioplayer.presentation.activity.MainVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        MainVM(
            storageManager = get(),
            playerManager = get()
        )
    }
}