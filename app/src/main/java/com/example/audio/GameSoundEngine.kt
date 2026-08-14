package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object GameSoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null
    var isSoundEnabled = true
    var isMusicEnabled = true

    private const val SAMPLE_RATE = 22050

    fun playSlash() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 120
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 600.0 - 450.0 * (i.toDouble() / numSamples)
                val noise = (Random.nextFloat() * 2 - 1) * 0.4
                val tone = sin(2.0 * PI * freq * t) * 0.6
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = ((tone + noise) * envelope * Short.MAX_VALUE * 0.7).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHit() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 90
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 220.0 - 150.0 * (i.toDouble() / numSamples)
                val noise = (Random.nextFloat() * 2 - 1) * 0.8
                val tone = sin(2.0 * PI * freq * t) * 0.4
                val envelope = exp(-i.toDouble() / (numSamples * 0.3))
                val sample = ((tone + noise) * envelope * Short.MAX_VALUE * 0.85).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playShieldBlock() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 180
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                // Metallic clang
                val tone1 = sin(2.0 * PI * 1200.0 * t) * 0.5
                val tone2 = sin(2.0 * PI * 1850.0 * t) * 0.3
                val tone3 = sin(2.0 * PI * 3400.0 * t) * 0.2
                val envelope = exp(-i.toDouble() / (numSamples * 0.2))
                val sample = ((tone1 + tone2 + tone3) * envelope * Short.MAX_VALUE * 0.75).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playDash() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 150
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val noise = (Random.nextFloat() * 2 - 1)
                val envelope = sin(progress * PI)
                val sample = (noise * envelope * Short.MAX_VALUE * 0.5).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playJump() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 100
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 200.0 + 350.0 * (i.toDouble() / numSamples)
                val tone = sin(2.0 * PI * freq * t)
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = (tone * envelope * Short.MAX_VALUE * 0.5).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playFireSkill() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 300
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val noise = (Random.nextFloat() * 2 - 1) * 0.7
                val rumble = sin(2.0 * PI * 90.0 * t) * 0.5
                val envelope = sin((i.toDouble() / numSamples) * PI)
                val sample = ((noise + rumble) * envelope * Short.MAX_VALUE * 0.8).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playLightningSkill() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 280
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = if ((i / 400) % 2 == 0) 1400.0 else 880.0
                val tone = sin(2.0 * PI * freq * t) * 0.6
                val noise = (Random.nextFloat() * 2 - 1) * 0.5
                val envelope = exp(-i.toDouble() / (numSamples * 0.35))
                val sample = ((tone + noise) * envelope * Short.MAX_VALUE * 0.8).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCoin() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 120
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = if (i < numSamples / 2) 987.77 else 1318.51 // B5 to E6
                val tone = sin(2.0 * PI * freq * t)
                val envelope = exp(-i.toDouble() / (numSamples * 0.4))
                val sample = (tone * envelope * Short.MAX_VALUE * 0.6).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playBossDefeated() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = listOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
            for (f in notes) {
                val numSamples = (SAMPLE_RATE * 150) / 1000
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val tone = sin(2.0 * PI * f * t) + sin(2.0 * PI * (f * 2) * t) * 0.3
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    buffer[i] = (tone * envelope * Short.MAX_VALUE * 0.5).toInt().toShort()
                }
                playPcm(buffer)
                delay(120)
            }
        }
    }

    fun playDramaticThunder() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 600
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val noise = (Random.nextFloat() * 2 - 1) * 0.8
                val lowRumble = sin(2.0 * PI * 55.0 * t) * 0.6
                val envelope = exp(-i.toDouble() / (numSamples * 0.5))
                val sample = ((noise + lowRumble) * envelope * Short.MAX_VALUE * 0.8).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            scope.launch {
                delay(buffer.size * 1000L / SAMPLE_RATE + 100)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    fun startBattleMusic() {
        if (!isMusicEnabled || musicJob?.isActive == true) return
        musicJob = scope.launch {
            val bassLine = listOf(110.0, 110.0, 130.81, 123.47, 98.0, 110.0, 146.83, 130.81)
            var idx = 0
            while (isActive && isMusicEnabled) {
                val freq = bassLine[idx % bassLine.size]
                val numSamples = (SAMPLE_RATE * 180) / 1000
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val tone = sin(2.0 * PI * freq * t) * 0.4 + sin(2.0 * PI * (freq * 0.5) * t) * 0.3
                    val drum = if (idx % 2 == 0 && i < numSamples / 4) (Random.nextFloat() * 2 - 1) * 0.25 else 0.0
                    val envelope = exp(-i.toDouble() / (numSamples * 0.4))
                    val sample = ((tone + drum) * envelope * Short.MAX_VALUE * 0.35).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playPcm(buffer)
                idx++
                delay(220)
            }
        }
    }

    fun stopBattleMusic() {
        musicJob?.cancel()
        musicJob = null
    }
}
