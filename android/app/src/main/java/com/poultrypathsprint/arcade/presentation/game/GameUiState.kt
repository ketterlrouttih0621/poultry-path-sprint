package com.poultrypathsprint.arcade.presentation.game

import com.poultrypathsprint.arcade.core.config.GameConfig
import com.poultrypathsprint.arcade.domain.model.RoadRow

data class GameUiState(
    val phase: GamePhase = GamePhase.READY,
    val rows: List<RoadRow> = emptyList(),
    val henRow: Int = GameConfig.START_ROW,
    val henLane: Int = 1,
    val cameraRow: Float = GameConfig.START_CAMERA_ROW,
    val henScale: Float = 1f,
    val bob: Float = 0f,
    val distance: Int = 0,
    val feathers: Int = 0,
    val barns: Int = 0,
    val banner: String = "",
    val stunned: Boolean = false,
    val highContrast: Boolean = false,
    val henTint: Int = 0
) {
    val controlsEnabled: Boolean
        get() = phase == GamePhase.RUNNING

    val boardEmpty: Boolean
        get() = rows.isEmpty()
}
