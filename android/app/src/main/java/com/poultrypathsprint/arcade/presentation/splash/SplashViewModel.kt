package com.poultrypathsprint.arcade.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poultrypathsprint.arcade.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _statusStep = MutableStateFlow(STEP_WARMING)
    val statusStep: StateFlow<Int> = _statusStep.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private var statusJob: Job? = null
    private var progressJob: Job? = null
    private var timerJob: Job? = null

    init {
        startStatusSequence()
        startProgressSequence()
        startNavigationTimer()
    }

    private fun startStatusSequence() {
        statusJob?.cancel()
        statusJob = viewModelScope.launch {
            delay(GameConfig.SPLASH_STATUS_TWO_MS)
            if (!_ready.value) {
                _statusStep.value = STEP_PAVING
            }
            delay(GameConfig.SPLASH_STATUS_THREE_MS - GameConfig.SPLASH_STATUS_TWO_MS)
            if (!_ready.value) {
                _statusStep.value = STEP_READY
            }
        }
    }

    private fun startProgressSequence() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            val steps = GameConfig.LOADER_DURATION_MS / PROGRESS_TICK_MS
            var current = 0L
            while (isActive && current < steps) {
                delay(PROGRESS_TICK_MS)
                current += 1
                val percent = (current * MAX_PERCENT / steps).toInt()
                _progress.value = percent.coerceIn(0, MAX_PERCENT)
            }
            _progress.value = MAX_PERCENT
        }
    }

    private fun startNavigationTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _statusStep.value = STEP_READY
            _progress.value = MAX_PERCENT
            _ready.value = true
        }
    }

    fun statusResource(step: Int): Int = when (step) {
        STEP_PAVING -> com.poultrypathsprint.arcade.R.string.splash_status_two
        STEP_READY -> com.poultrypathsprint.arcade.R.string.splash_status_three
        else -> com.poultrypathsprint.arcade.R.string.splash_status_one
    }

    override fun onCleared() {
        super.onCleared()
        statusJob?.cancel()
        progressJob?.cancel()
        timerJob?.cancel()
        statusJob = null
        progressJob = null
        timerJob = null
    }

    companion object {
        const val STEP_WARMING = 0
        const val STEP_PAVING = 1
        const val STEP_READY = 2
        private const val PROGRESS_TICK_MS = 80L
        private const val MAX_PERCENT = 100
    }
}
