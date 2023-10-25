package com.example.audioplayer.presentation.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.PlayerManager
import com.example.audioplayer.data.StorageManager
import com.example.audioplayer.entity.Track
import com.example.audioplayer.presentation.service.AudioPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainVM(
    private val storageManager: StorageManager,
    private val playerManager: PlayerManager
) : ViewModel() {

    private var mBound = false

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

    suspend fun getById(id: Long): Track? {
        return storageManager.getById(id).single()
    }

    fun play(id: Long) {
        playerManager.play(id)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioBinder
            binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }
    fun bind(context: Context) {
        Intent(context, AudioPlayerService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbind(context: Context) {
        context.unbindService(serviceConnection)
        mBound = false
    }
}
