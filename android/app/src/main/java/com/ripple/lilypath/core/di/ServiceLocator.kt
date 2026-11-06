package com.ripple.lilypath.core.di

import android.content.Context
import com.ripple.lilypath.data.local.PondFeedback
import com.ripple.lilypath.data.local.PondPreferences
import com.ripple.lilypath.data.repository.ProgressRepositoryImpl
import com.ripple.lilypath.data.repository.SettingsRepositoryImpl
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.repository.SettingsRepository
import com.ripple.lilypath.domain.usecase.GeneratePondUseCase
import com.ripple.lilypath.domain.usecase.NextHintUseCase
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase
import com.ripple.lilypath.domain.usecase.ValidateMoveUseCase

object ServiceLocator {

    val generatePond = GeneratePondUseCase()
    val validateMove = ValidateMoveUseCase()
    val nextHint = NextHintUseCase(validateMove)
    val unlockSeason = UnlockSeasonUseCase()

    lateinit var progress: ProgressRepository
        private set

    lateinit var settings: SettingsRepository
        private set

    lateinit var feedback: PondFeedback
        private set

    fun init(context: Context) {
        if (::progress.isInitialized) return
        val preferences = PondPreferences(context.applicationContext)
        progress = ProgressRepositoryImpl(preferences, unlockSeason)
        settings = SettingsRepositoryImpl(preferences)
        feedback = PondFeedback(context.applicationContext)
    }
}
