package com.poultrypathsprint.arcade.domain.model

data class GameSettings(
    val soundEnabled: Boolean = true,
    val shakeEnabled: Boolean = true,
    val highContrast: Boolean = false,
    val hopSensitivity: Int = 2
)
