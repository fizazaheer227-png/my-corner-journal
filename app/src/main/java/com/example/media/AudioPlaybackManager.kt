package com.example.media

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlaybackManager {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private var currentPlayingPath: String? = null

    fun play(filePath: String, onCompletion: () -> Unit = {}) {
        if (filePath.isBlank()) return
        val file = File(filePath)
        if (!file.exists() && !filePath.startsWith("content://")) return

        try {
            if (currentPlayingPath == filePath && mediaPlayer != null) {
                if (_isPlaying.value) {
                    pause()
                } else {
                    resume()
                }
                return
            }

            stop()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                _duration.value = this.duration
                start()
                _isPlaying.value = true
                currentPlayingPath = filePath

                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = 0
                    stopProgressTracking()
                    onCompletion()
                }
            }

            startProgressTracking()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing audio file $filePath", e)
            stop()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    _isPlaying.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error pausing", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let {
                it.start()
                _isPlaying.value = true
                startProgressTracking()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error resuming", e)
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _currentPosition.value = positionMs
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error seeking", e)
        }
    }

    fun stop() {
        stopProgressTracking()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPosition.value = 0
            _duration.value = 0
            currentPlayingPath = null
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _currentPosition.value = it.currentPosition
                    }
                }
                delay(200)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stop()
    }
}
