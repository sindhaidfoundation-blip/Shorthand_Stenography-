package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class CourtAudioState(
    val sourceLabel: String? = null,
    val isLoaded: Boolean = false,
    val isPlaying: Boolean = false,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)

class CourtAudioPlayer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(CourtAudioState())
    val state: StateFlow<CourtAudioState> = _state.asStateFlow()

    fun loadUri(uri: Uri, label: String) {
        releasePlayer()
        try {
            val player = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
                setOnCompletionListener {
                    tickerJob?.cancel()
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        positionMs = 0
                    )
                }
            }
            mediaPlayer = player
            applySpeedToPlayer(player, _state.value.playbackSpeed)
            _state.value = _state.value.copy(
                sourceLabel = label,
                isLoaded = true,
                isPlaying = false,
                positionMs = 0,
                durationMs = player.duration.coerceAtLeast(0),
                errorMessage = null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoaded = false,
                isPlaying = false,
                errorMessage = "Unable to decode selected audio file"
            )
        }
    }

    fun loadFile(file: File, label: String) {
        releasePlayer()
        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    tickerJob?.cancel()
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        positionMs = 0
                    )
                }
            }
            mediaPlayer = player
            applySpeedToPlayer(player, _state.value.playbackSpeed)
            _state.value = _state.value.copy(
                sourceLabel = label,
                isLoaded = true,
                isPlaying = false,
                positionMs = 0,
                durationMs = player.duration.coerceAtLeast(0),
                errorMessage = null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoaded = false,
                isPlaying = false,
                errorMessage = "Unable to play synthesized court audio"
            )
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            tickerJob?.cancel()
            _state.value = _state.value.copy(isPlaying = false)
        } else {
            applySpeedToPlayer(player, _state.value.playbackSpeed)
            player.start()
            _state.value = _state.value.copy(isPlaying = true)
            startTicker()
        }
    }

    fun seekTo(ms: Int) {
        val player = mediaPlayer ?: return
        val clamped = ms.coerceIn(0, _state.value.durationMs)
        player.seekTo(clamped)
        _state.value = _state.value.copy(positionMs = clamped)
    }

    fun setSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.65f, 1.75f)
        _state.value = _state.value.copy(playbackSpeed = clamped)
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                applySpeedToPlayer(player, clamped)
            }
        }
    }

    private fun applySpeedToPlayer(player: MediaPlayer, speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                player.playbackParams = PlaybackParams().setSpeed(speed)
            } catch (_: Exception) {
                // Some codecs reject extreme speeds while paused; ignore gracefully
            }
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    _state.value = _state.value.copy(
                        positionMs = player.currentPosition.coerceAtLeast(0)
                    )
                }
                delay(150L)
            }
        }
    }

    private fun releasePlayer() {
        tickerJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun release() {
        releasePlayer()
    }
}
