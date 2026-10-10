package com.poultrypathsprint.arcade.domain.usecase

import com.poultrypathsprint.arcade.domain.repository.RunRepository

class SaveRunResultUseCase(private val runRepository: RunRepository) {

    operator fun invoke(distance: Int, feathers: Int, barns: Int): Int {
        return runRepository.saveRun(distance, feathers, barns)
    }
}
