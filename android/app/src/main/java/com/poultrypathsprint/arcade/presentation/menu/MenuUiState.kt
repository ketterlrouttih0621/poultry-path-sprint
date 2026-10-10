package com.poultrypathsprint.arcade.presentation.menu

data class MenuUiState(
    val bestDistance: Int = 0,
    val feathers: Int = 0,
    val runs: Int = 0,
    val equippedName: String = "",
    val equippedAccent: Int = 0
)
