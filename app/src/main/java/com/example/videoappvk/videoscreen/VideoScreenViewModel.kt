package com.example.videoappvk.videoscreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class VideoScreenViewModel : ViewModel() {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer
        get() = _player ?: throw IllegalStateException("Player is not initialized")

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private var playbackPosition: Long = 0
    private var currentMediaItemIndex: Int = 0
    private var isPlayerPlaying: Boolean = true

    private val playbackListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_ENDED) restartPlayback()
        }
    }

    fun initializePlayer(context: Context, videoUrl: String) {
        if (_player == null) {
            _player = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                setMediaItem(mediaItem)
                prepare()
                seekTo(currentMediaItemIndex, playbackPosition)
                playWhenReady = isPlayerPlaying
                addListener(playbackListener)
            }

            _isPlaying.value = true
        }
    }

    fun togglePlayback() {
        _player?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.play()
                _isPlaying.value = true
            }
        }
    }

    fun seekBackward() {
        _player?.seekTo((_player?.currentPosition ?: 0) - 10_000)
    }

    fun seekForward() {
        _player?.seekTo((_player?.currentPosition ?: 0) + 10_000)
    }

    private fun restartPlayback() {
        _player?.seekTo(0)
        _player?.play()
        _isPlaying.value = true
    }

    fun savePlayerState() {
        _player?.let {
            playbackPosition = it.currentPosition
            currentMediaItemIndex = it.currentMediaItemIndex
            isPlayerPlaying = it.isPlaying
        }
    }

    fun releasePlayer() {
        _player?.removeListener(playbackListener)
        _player?.release()
        _player = null
    }

    public override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
