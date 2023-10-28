package com.example.audioplayer.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.PlayerManager
import com.example.audioplayer.data.StorageManager
import com.example.audioplayer.entity.Track
import com.example.audioplayer.presentation.AudioPlayerReceiver
import com.example.audioplayer.presentation.service.PlayerConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainVM(
    private val storageManager: StorageManager,
    private val playerManager: PlayerManager,
    private val bReceiver: AudioPlayerReceiver
) : ViewModel() {

//    private var mBound = false

    private val _listContent: MutableLiveData<List<Track>> = MutableLiveData(emptyList())
    val listContent: LiveData<List<Track>>
        get() = _listContent
    private var currentInd = -1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _listContent.postValue(
                storageManager.getTrackFlow().filterNotNull().toList()
            )
        }
    }

    fun configurePlayer() {
        val onNextListener = {
            currentInd = if (currentInd < listContent.value!!.lastIndex) currentInd + 1 else 0
            listContent.value!![currentInd].id
        }
        val onPreviousListener = {
            currentInd = if (currentInd == 0) listContent.value!!.lastIndex else currentInd - 1
            listContent.value!![currentInd].id
        }
        playerManager.configurePlayer(onNextListener, onPreviousListener)
    }
    fun play(id: Long) {
        currentInd = listContent.value?.indexOfFirst { it.id == id } ?: -1
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.getUriById(id).single()?.also { uri -> playerManager.play(uri) }
        }
    }

    fun startService(context: Context) {
        val intent = Intent(PlayerConstants.ACTION_START_SERVICE)
        context.sendBroadcast(intent)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver(context: Context) {
        val intentFilter = IntentFilter().apply {
                addAction(PlayerConstants.ACTION_NEXT)
                addAction(PlayerConstants.ACTION_PREVIOUS)
                addAction(PlayerConstants.ACTION_START_SERVICE)
                addAction(PlayerConstants.ACTION_PAUSE)
                addAction(PlayerConstants.ACTION_PLAY)
                addAction(PlayerConstants.ACTION_STOP_SERVICE)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                bReceiver,
                intentFilter,
                RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(
                bReceiver,
                intentFilter)
        }

    }

//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as AudioPlayerService.AudioBinder
//            binder.getService()
//            mBound = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            mBound = false
//        }
//    }


//
//    fun bind(context: Context) {
//        Intent(context, AudioPlayerService::class.java).also { intent ->
//            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//        }
//    }
//
//    fun unbind(context: Context) {
//        context.unbindService(serviceConnection)
//        mBound = false
//    }
}
