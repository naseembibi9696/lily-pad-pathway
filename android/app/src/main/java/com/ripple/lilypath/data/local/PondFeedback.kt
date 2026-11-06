package com.ripple.lilypath.data.local

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.ripple.lilypath.data.sample.SampleData

class PondFeedback(context: Context) {

    private val appContext = context.applicationContext
    private var generator: ToneGenerator? = null
    private var generatorVolume = -1

    fun playNote(step: Int, volume: Int) {
        if (volume <= 0) return
        val tone = SampleData.pentatonicTones[step.mod(SampleData.pentatonicTones.size)]
        runCatching {
            val active = toneGenerator(volume)
            active?.startTone(tone, NOTE_MS)
        }
    }

    fun tick(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val vibrator = vibrator() ?: return@runCatching
            if (!vibrator.hasVibrator()) return@runCatching
            vibrator.vibrate(VibrationEffect.createOneShot(TICK_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun release() {
        runCatching { generator?.release() }
        generator = null
        generatorVolume = -1
    }

    private fun toneGenerator(volume: Int): ToneGenerator? {
        val wanted = volume.coerceIn(1, 100)
        if (generator == null || generatorVolume != wanted) {
            runCatching { generator?.release() }
            generator = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, wanted) }.getOrNull()
            generatorVolume = wanted
        }
        return generator
    }

    private fun vibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = appContext.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            appContext.getSystemService(Vibrator::class.java)
        }

    private companion object {
        const val NOTE_MS = 110
        const val TICK_MS = 18L
    }
}
