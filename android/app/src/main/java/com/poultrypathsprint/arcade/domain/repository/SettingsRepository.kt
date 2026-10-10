package com.poultrypathsprint.arcade.domain.repository

import com.poultrypathsprint.arcade.domain.model.GameSettings

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}
