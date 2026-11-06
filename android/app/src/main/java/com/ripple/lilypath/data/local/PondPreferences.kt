package com.ripple.lilypath.data.local

import android.content.Context
import android.content.SharedPreferences

class PondPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun solvedPonds(): Set<Int> =
        prefs.getStringSet(KEY_SOLVED, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun storeSolved(values: Set<Int>) {
        prefs.edit().putStringSet(KEY_SOLVED, values.map { it.toString() }.toSet()).apply()
    }

    fun hints(index: Int): Int = prefs.getInt(KEY_HINTS + index, 0)

    fun storeHints(index: Int, value: Int) {
        prefs.edit().putInt(KEY_HINTS + index, value).apply()
    }

    fun tutorialDone(): Boolean = prefs.getBoolean(KEY_TUTORIAL, false)

    fun storeTutorialDone(value: Boolean) {
        prefs.edit().putBoolean(KEY_TUTORIAL, value).apply()
    }

    fun volume(): Int = prefs.getInt(KEY_VOLUME, DEFAULT_VOLUME)

    fun storeVolume(value: Int) {
        prefs.edit().putInt(KEY_VOLUME, value).apply()
    }

    fun haptics(): Boolean = prefs.getBoolean(KEY_HAPTICS, true)

    fun storeHaptics(value: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, value).apply()
    }

    fun hintFrequency(): String = prefs.getString(KEY_HINT_FREQUENCY, DEFAULT_HINTS).orEmpty()

    fun storeHintFrequency(value: String) {
        prefs.edit().putString(KEY_HINT_FREQUENCY, value).apply()
    }

    fun clearProgress() {
        val editor = prefs.edit()
        editor.remove(KEY_SOLVED)
        editor.remove(KEY_TUTORIAL)
        prefs.all.keys.filter { it.startsWith(KEY_HINTS) }.forEach { editor.remove(it) }
        editor.apply()
    }

    private companion object {
        const val STORE = "lily_pad_pathway"
        const val KEY_SOLVED = "solved_ponds"
        const val KEY_HINTS = "hints_pond_"
        const val KEY_TUTORIAL = "tutorial_done"
        const val KEY_VOLUME = "volume"
        const val KEY_HAPTICS = "haptics"
        const val KEY_HINT_FREQUENCY = "hint_frequency"
        const val DEFAULT_VOLUME = 70
        const val DEFAULT_HINTS = "SOMETIMES"
    }
}
