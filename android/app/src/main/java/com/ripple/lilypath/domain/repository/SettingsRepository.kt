package com.ripple.lilypath.domain.repository

import com.ripple.lilypath.domain.model.HintFrequency

interface SettingsRepository {

    fun volume(): Int

    fun setVolume(value: Int)

    fun haptics(): Boolean

    fun setHaptics(enabled: Boolean)

    fun hintFrequency(): HintFrequency

    fun setHintFrequency(value: HintFrequency)
}
