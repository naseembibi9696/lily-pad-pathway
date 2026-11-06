package com.ripple.lilypath.data.repository

import com.ripple.lilypath.data.local.PondPreferences
import com.ripple.lilypath.domain.model.HintFrequency
import com.ripple.lilypath.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: PondPreferences
) : SettingsRepository {

    override fun volume(): Int = preferences.volume()

    override fun setVolume(value: Int) {
        preferences.storeVolume(value.coerceIn(0, 100))
    }

    override fun haptics(): Boolean = preferences.haptics()

    override fun setHaptics(enabled: Boolean) {
        preferences.storeHaptics(enabled)
    }

    override fun hintFrequency(): HintFrequency =
        HintFrequency.entries.firstOrNull { it.name == preferences.hintFrequency() }
            ?: HintFrequency.SOMETIMES

    override fun setHintFrequency(value: HintFrequency) {
        preferences.storeHintFrequency(value.name)
    }
}
