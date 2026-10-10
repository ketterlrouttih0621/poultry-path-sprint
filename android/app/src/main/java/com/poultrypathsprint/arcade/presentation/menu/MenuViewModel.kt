package com.poultrypathsprint.arcade.presentation.menu

import androidx.lifecycle.ViewModel
import com.poultrypathsprint.arcade.domain.repository.OutfitRepository
import com.poultrypathsprint.arcade.domain.repository.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val runRepository: RunRepository,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val equipped = outfitRepository.outfits().firstOrNull { it.id == outfitRepository.equippedId() }
        _state.value = MenuUiState(
            bestDistance = runRepository.bestDistance(),
            feathers = runRepository.totalFeathers(),
            runs = runRepository.totalRuns(),
            equippedName = equipped?.name ?: "",
            equippedAccent = equipped?.accentColor ?: 0
        )
    }
}
