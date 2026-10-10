package com.poultrypathsprint.arcade.domain.usecase

import com.poultrypathsprint.arcade.domain.model.Outfit
import com.poultrypathsprint.arcade.domain.repository.OutfitRepository

class UnlockOutfitUseCase(private val outfitRepository: OutfitRepository) {

    operator fun invoke(totalFeathers: Int): Outfit? {
        val owned = outfitRepository.unlockedIds()
        val affordable = outfitRepository.outfits()
            .filter { it.cost <= totalFeathers && !owned.contains(it.id) }
            .maxByOrNull { it.cost }
        if (affordable != null) {
            outfitRepository.unlock(affordable.id)
        }
        return affordable
    }
}
