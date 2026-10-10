package com.poultrypathsprint.arcade.core.di

import android.content.Context
import com.poultrypathsprint.arcade.data.local.PoultryPreferences
import com.poultrypathsprint.arcade.data.repository.OutfitRepositoryImpl
import com.poultrypathsprint.arcade.data.repository.RunRepositoryImpl
import com.poultrypathsprint.arcade.data.repository.SettingsRepositoryImpl
import com.poultrypathsprint.arcade.domain.repository.OutfitRepository
import com.poultrypathsprint.arcade.domain.repository.RunRepository
import com.poultrypathsprint.arcade.domain.repository.SettingsRepository
import com.poultrypathsprint.arcade.domain.usecase.AdvanceRunUseCase
import com.poultrypathsprint.arcade.domain.usecase.GenerateRoadUseCase
import com.poultrypathsprint.arcade.domain.usecase.SaveRunResultUseCase
import com.poultrypathsprint.arcade.domain.usecase.UnlockOutfitUseCase

object ServiceLocator {

    private var preferences: PoultryPreferences? = null

    fun init(context: Context) {
        if (preferences == null) {
            preferences = PoultryPreferences(context.applicationContext)
        }
    }

    fun preferences(context: Context): PoultryPreferences {
        init(context)
        return preferences ?: PoultryPreferences(context.applicationContext)
    }

    fun runRepository(context: Context): RunRepository = RunRepositoryImpl(preferences(context))

    fun outfitRepository(context: Context): OutfitRepository =
        OutfitRepositoryImpl(preferences(context))

    fun settingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(preferences(context))

    fun generateRoadUseCase(): GenerateRoadUseCase = GenerateRoadUseCase()

    fun advanceRunUseCase(): AdvanceRunUseCase = AdvanceRunUseCase()

    fun saveRunResultUseCase(context: Context): SaveRunResultUseCase =
        SaveRunResultUseCase(runRepository(context))

    fun unlockOutfitUseCase(context: Context): UnlockOutfitUseCase =
        UnlockOutfitUseCase(outfitRepository(context))
}
