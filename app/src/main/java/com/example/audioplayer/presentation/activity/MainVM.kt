package com.example.audioplayer.presentation.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.StorageManager
import com.example.audioplayer.entity.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainVM(
    storageManager: StorageManager
) : ViewModel() {

    private val _listContent: MutableLiveData<List<Track>> = MutableLiveData(emptyList())

    val listContent: LiveData<List<Track>>
        get() = _listContent

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _listContent.postValue(
                storageManager.getTrackFlow().filterNotNull().toList()
            )
        }
    }


}