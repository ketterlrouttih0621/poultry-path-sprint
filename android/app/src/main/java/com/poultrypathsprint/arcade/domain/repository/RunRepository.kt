package com.poultrypathsprint.arcade.domain.repository

interface RunRepository {
    fun bestDistance(): Int
    fun totalFeathers(): Int
    fun totalBarns(): Int
    fun totalRuns(): Int
    fun saveRun(distance: Int, feathers: Int, barns: Int): Int
    fun resetProgress()
}
