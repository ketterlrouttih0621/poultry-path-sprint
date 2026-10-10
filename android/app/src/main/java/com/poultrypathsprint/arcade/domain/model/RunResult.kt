package com.poultrypathsprint.arcade.domain.model

data class RunResult(
    val distance: Int,
    val feathers: Int,
    val barns: Int,
    val bestDistance: Int,
    val newBest: Boolean,
    val unlockedOutfit: String,
    val crashLine: String
)
