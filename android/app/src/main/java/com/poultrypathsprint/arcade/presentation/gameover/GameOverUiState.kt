package com.poultrypathsprint.arcade.presentation.gameover

data class GameOverUiState(
    val distance: Int = 0,
    val feathers: Int = 0,
    val barns: Int = 0,
    val bestDistance: Int = 0,
    val newBest: Boolean = false,
    val unlockedOutfit: String = "",
    val crashLine: String = "",
    val totalFeathers: Int = 0
)
