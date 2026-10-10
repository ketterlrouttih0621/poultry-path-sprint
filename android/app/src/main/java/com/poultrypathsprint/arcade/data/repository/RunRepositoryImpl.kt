package com.poultrypathsprint.arcade.data.repository

import com.poultrypathsprint.arcade.data.local.PoultryPreferences
import com.poultrypathsprint.arcade.domain.repository.RunRepository

class RunRepositoryImpl(private val preferences: PoultryPreferences) : RunRepository {

    override fun bestDistance(): Int =
        preferences.readInt(PoultryPreferences.KEY_BEST_DISTANCE, 0)

    override fun totalFeathers(): Int =
        preferences.readInt(PoultryPreferences.KEY_FEATHERS_TOTAL, 0)

    override fun totalBarns(): Int =
        preferences.readInt(PoultryPreferences.KEY_BARNS_TOTAL, 0)

    override fun totalRuns(): Int =
        preferences.readInt(PoultryPreferences.KEY_RUNS_TOTAL, 0)

    override fun saveRun(distance: Int, feathers: Int, barns: Int): Int {
        val best = maxOf(bestDistance(), distance)
        preferences.writeInt(PoultryPreferences.KEY_BEST_DISTANCE, best)
        preferences.writeInt(PoultryPreferences.KEY_FEATHERS_TOTAL, totalFeathers() + feathers)
        preferences.writeInt(PoultryPreferences.KEY_BARNS_TOTAL, totalBarns() + barns)
        preferences.writeInt(PoultryPreferences.KEY_RUNS_TOTAL, totalRuns() + 1)
        return best
    }

    override fun resetProgress() {
        preferences.clearProgress()
    }
}
