package com.example.audioplayer.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.presentation.activity.adapter.TrackAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }

    private val viewModel: MainVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.registerReceiver(applicationContext)
        viewModel.configurePlayer()

        binding.contentList.adapter = TrackAdapter {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.play(it.id)
                viewModel.startService(applicationContext)
            }
        }
        viewModel.listContent.observe(this@MainActivity) { trackList ->
            (binding.contentList.adapter as TrackAdapter).setContent(trackList)
        }

    }

    override fun onStart() {
        super.onStart()
//        viewModel.bind(this)
    }

    override fun onStop() {
        super.onStop()
//        viewModel.unbind(this)
    }

}