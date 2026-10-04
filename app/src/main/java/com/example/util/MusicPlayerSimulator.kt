package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin

object MusicPlayerSimulator {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow("Janji Suci - Romantic Instrumental")
    val currentTrack: StateFlow<String> = _currentTrack.asStateFlow()

    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun togglePlay(trackName: String = "Janji Suci - Romantic Instrumental") {
        if (_isPlaying.value) {
            pause()
        } else {
            play(trackName)
        }
    }

    fun play(trackName: String = "Janji Suci - Romantic Instrumental") {
        _currentTrack.value = trackName
        _isPlaying.value = true
        playbackJob?.cancel()

        // Produce a gentle pleasant soothing chord sequence using simple sine wave synthesis
        playbackJob = scope.launch {
            try {
                val sampleRate = 22050
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                // Romantic chord frequencies: C major, G major, A minor, F major (Canon progression)
                val melodyFrequencies = listOf(
                    listOf(523.25, 659.25, 783.99), // C, E, G
                    listOf(392.00, 493.88, 587.33), // G, B, D
                    listOf(440.00, 523.25, 659.25), // A, C, E
                    listOf(349.23, 440.00, 523.25)  // F, A, C
                )

                while (_isPlaying.value) {
                    for (chord in melodyFrequencies) {
                        if (!_isPlaying.value) break
                        val durationMs = 1200
                        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                        val samples = ShortArray(numSamples)

                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            val envelope = (1.0 - (i.toDouble() / numSamples)) * 0.15 // gentle fade
                            var wave = 0.0
                            for (freq in chord) {
                                wave += sin(2.0 * Math.PI * freq * time)
                            }
                            samples[i] = ((wave / chord.size) * envelope * Short.MAX_VALUE).toInt().toShort()
                        }
                        audioTrack.write(samples, 0, numSamples)
                        delay(50)
                    }
                }

                audioTrack.stop()
                audioTrack.release()
            } catch (e: Throwable) {
                // In headless or audio-restricted container, fail silently
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }
}
