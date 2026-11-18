package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class CadenceTestStatus {
    READY,
    TESTING,
    CALIBRATED
}

data class CadenceAcousticState(
    val testStatus: CadenceTestStatus = CadenceTestStatus.READY,
    val currentBeatIndex: Int = 0,
    val isAmbientActive: Boolean = false
)

class CadenceAcousticEngine(
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(CadenceAcousticState())
    val state: StateFlow<CadenceAcousticState> = _state.asStateFlow()

    private var toneGenerator: ToneGenerator? = null
    private var testJob: Job? = null
    private var ambientJob: Job? = null
    private var audioTrack: AudioTrack? = null

    private fun getOrCreateToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 65)
            } catch (_: Throwable) {
                null
            }
        }
        return toneGenerator
    }

    fun playQuarterMinuteChime() {
        try {
            getOrCreateToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 160)
        } catch (_: Throwable) {
        }
    }

    fun playMetronomeTick(accent: Boolean = false) {
        try {
            val tone = if (accent) ToneGenerator.TONE_PROP_BEEP else ToneGenerator.TONE_SUP_PIP
            getOrCreateToneGenerator()?.startTone(tone, if (accent) 70 else 45)
        } catch (_: Throwable) {
        }
    }

    fun toggleFiveSecondCadenceTest(wpm: Int) {
        if (_state.value.testStatus == CadenceTestStatus.TESTING) {
            stopCadenceTest(markCalibrated = false)
            return
        }
        startFiveSecondCadenceTest(wpm)
    }

    fun startFiveSecondCadenceTest(wpm: Int) {
        testJob?.cancel()
        val safeWpm = wpm.coerceIn(60, 180)
        val intervalMs = (60_000L / safeWpm).coerceIn(280L, 1000L)

        _state.value = _state.value.copy(
            testStatus = CadenceTestStatus.TESTING,
            currentBeatIndex = 0
        )

        testJob = scope.launch {
            val startTime = System.currentTimeMillis()
            var beat = 0
            while (isActive && System.currentTimeMillis() - startTime < 5000L) {
                playMetronomeTick(accent = beat % 4 == 0)
                _state.value = _state.value.copy(currentBeatIndex = beat)
                beat++
                delay(intervalMs)
            }
            _state.value = _state.value.copy(
                testStatus = CadenceTestStatus.CALIBRATED,
                currentBeatIndex = 0
            )
        }
    }

    fun stopCadenceTest(markCalibrated: Boolean = false) {
        testJob?.cancel()
        testJob = null
        _state.value = _state.value.copy(
            testStatus = if (markCalibrated) CadenceTestStatus.CALIBRATED else CadenceTestStatus.READY,
            currentBeatIndex = 0
        )
    }

    fun setAmbientMaskingActive(active: Boolean) {
        if (active == _state.value.isAmbientActive) return
        _state.value = _state.value.copy(isAmbientActive = active)
        if (active) {
            startAmbientCourtroomMasking()
        } else {
            stopAmbientCourtroomMasking()
        }
    }

    private fun startAmbientCourtroomMasking() {
        ambientJob?.cancel()
        ambientJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 16000
            val minBuffer = try {
                AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
            } catch (_: Throwable) {
                -1
            }
            if (minBuffer <= 0) return@launch

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuffer * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Throwable) {
                null
            } ?: return@launch

            audioTrack = track
            try {
                track.play()
                val buffer = ShortArray(sampleRate / 10) // 100ms chunk
                var prevSample = 0.0
                var phase = 0.0
                var tickCounter = 0

                while (isActive && _state.value.isAmbientActive) {
                    tickCounter++
                    val gavelPulse = (tickCounter % 75 == 0)
                    for (i in buffer.indices) {
                        // Gentle courtroom ceiling-fan hum + soft gallery murmur noise
                        val white = Random.nextDouble(-1.0, 1.0)
                        prevSample = prevSample * 0.92 + white * 0.08
                        phase += 2.0 * PI * 110.0 / sampleRate
                        val hum = sin(phase) * 0.018
                        val murmur = prevSample * 0.045
                        val tap = if (gavelPulse && i < 240) {
                            sin(i * 0.35) * (1.0 - i / 240.0) * 0.14
                        } else {
                            0.0
                        }
                        val combined = ((hum + murmur + tap) * Short.MAX_VALUE * 0.35)
                            .toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        buffer[i] = combined.toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Throwable) {
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {
                }
                if (audioTrack === track) {
                    audioTrack = null
                }
            }
        }
    }

    private fun stopAmbientCourtroomMasking() {
        ambientJob?.cancel()
        ambientJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Throwable) {
        }
        audioTrack = null
    }

    fun release() {
        testJob?.cancel()
        stopAmbientCourtroomMasking()
        try {
            toneGenerator?.release()
        } catch (_: Throwable) {
        }
        toneGenerator = null
    }
}
