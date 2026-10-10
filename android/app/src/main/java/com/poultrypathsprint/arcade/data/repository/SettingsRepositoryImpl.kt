package com.poultrypathsprint.arcade.data.repository

import com.poultrypathsprint.arcade.data.local.PoultryPreferences
import com.poultrypathsprint.arcade.domain.model.GameSettings
import com.poultrypathsprint.arcade.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: PoultryPreferences) : SettingsRepository {

    override fun load(): GameSettings = GameSettings(
        soundEnabled = preferences.readBoolean(PoultryPreferences.KEY_SOUND, true),
        shakeEnabled = preferences.readBoolean(PoultryPreferences.KEY_SHAKE, true),
        highContrast = preferences.readBoolean(PoultryPreferences.KEY_CONTRAST, false),
        hopSensitivity = preferences.readInt(PoultryPreferences.KEY_SENSITIVITY, 2)
    )

    override fun save(settings: GameSettings) {
        preferences.writeBoolean(PoultryPreferences.KEY_SOUND, settings.soundEnabled)
        preferences.writeBoolean(PoultryPreferences.KEY_SHAKE, settings.shakeEnabled)
        preferences.writeBoolean(PoultryPreferences.KEY_CONTRAST, settings.highContrast)
        preferences.writeInt(PoultryPreferences.KEY_SENSITIVITY, settings.hopSensitivity)
    }
}
