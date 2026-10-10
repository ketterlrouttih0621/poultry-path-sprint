package com.poultrypathsprint.arcade.presentation.gameover

import androidx.lifecycle.ViewModel
import com.poultrypathsprint.arcade.domain.repository.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(private val runRepository: RunRepository) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var loaded = false

    fun load(
        distance: Int,
        feathers: Int,
        barns: Int,
        bestDistance: Int,
        newBest: Boolean,
        unlockedOutfit: String,
        crashLine: String
    ) {
        if (loaded) {
            return
        }
        loaded = true
        _state.value = GameOverUiState(
            distance = distance,
            feathers = feathers,
            barns = barns,
            bestDistance = maxOf(bestDistance, runRepository.bestDistance()),
            newBest = newBest,
            unlockedOutfit = unlockedOutfit,
            crashLine = crashLine,
            totalFeathers = runRepository.totalFeathers()
        )
    }
}
