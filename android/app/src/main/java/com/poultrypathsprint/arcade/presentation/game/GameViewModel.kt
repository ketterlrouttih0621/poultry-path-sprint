package com.poultrypathsprint.arcade.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poultrypathsprint.arcade.core.config.GameConfig
import com.poultrypathsprint.arcade.data.sample.SampleData
import com.poultrypathsprint.arcade.domain.model.ObstacleKind
import com.poultrypathsprint.arcade.domain.model.RoadRow
import com.poultrypathsprint.arcade.domain.model.RunResult
import com.poultrypathsprint.arcade.domain.repository.OutfitRepository
import com.poultrypathsprint.arcade.domain.repository.RunRepository
import com.poultrypathsprint.arcade.domain.repository.SettingsRepository
import com.poultrypathsprint.arcade.domain.usecase.AdvanceRunUseCase
import com.poultrypathsprint.arcade.domain.usecase.GenerateRoadUseCase
import com.poultrypathsprint.arcade.domain.usecase.SaveRunResultUseCase
import com.poultrypathsprint.arcade.domain.usecase.UnlockOutfitUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class GameViewModel(
    private val generateRoad: GenerateRoadUseCase,
    private val advanceRun: AdvanceRunUseCase,
    private val saveRunResult: SaveRunResultUseCase,
    private val unlockOutfit: UnlockOutfitUseCase,
    private val runRepository: RunRepository,
    outfitRepository: OutfitRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _result = MutableStateFlow<RunResult?>(null)
    val result: StateFlow<RunResult?> = _result.asStateFlow()

    private val rows = mutableListOf<RoadRow>()
    private val highContrast = settingsRepository.load().highContrast
    private val henTint = outfitRepository.outfits()
        .firstOrNull { it.id == outfitRepository.equippedId() }
        ?.accentColor
        ?: 0

    private var henRow = GameConfig.START_ROW
    private var henLane = 1
    private var cameraRow = GameConfig.START_CAMERA_ROW
    private var distance = 0
    private var feathers = 0
    private var barns = 0
    private var scrollSpeed = GameConfig.SCROLL_DP_PER_SEC_START
    private var phase = GamePhase.READY
    private var elapsedMs = 0L
    private var stunUntilMs = 0L
    private var stunnedRow = Int.MIN_VALUE
    private var checkpointUntilMs = 0L
    private var bannerUntilMs = 0L
    private var hopUntilMs = 0L
    private var banner = ""
    private var finished = false
    private var loopJob: Job? = null

    init {
        ensureRows()
        publish()
        startLoop()
    }

    fun onHop() {
        if (phase != GamePhase.RUNNING) {
            return
        }
        henRow += 1
        distance += 1
        hopUntilMs = elapsedMs + GameConfig.HOP_ANIM_MS
        ensureRows()
        if (henRow > 0 && henRow % GameConfig.BARN_EVERY_ROWS == 0) {
            barns += 1
            scrollSpeed = (scrollSpeed + GameConfig.SCROLL_DP_PER_SEC_STEP)
                .coerceAtMost(GameConfig.SCROLL_DP_PER_SEC_MAX)
            checkpointUntilMs = elapsedMs + GameConfig.CHECKPOINT_HOLD_MS
            bannerUntilMs = elapsedMs + GameConfig.BANNER_VISIBLE_MS
            banner = rowAt(henRow)?.checkpointName.orEmpty()
        }
        collectFeather()
        if (!resolveCollision()) {
            publish()
        }
    }

    fun onLane(delta: Int) {
        if (phase != GamePhase.RUNNING) {
            return
        }
        val next = (henLane + delta).coerceIn(0, GameConfig.LANES - 1)
        if (next == henLane) {
            return
        }
        henLane = next
        collectFeather()
        if (!resolveCollision()) {
            publish()
        }
    }

    fun pause() {
        if (phase == GamePhase.RUNNING || phase == GamePhase.STUNNED) {
            phase = GamePhase.PAUSED
            publish()
        }
    }

    fun resumeRun() {
        if (phase == GamePhase.PAUSED) {
            phase = GamePhase.RUNNING
            publish()
        }
    }

    fun abandon() {
        finished = true
        loopJob?.cancel()
        loopJob = null
    }

    private fun startLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            delay(GameConfig.READY_HOLD_MS)
            if (phase == GamePhase.READY) {
                phase = GamePhase.RUNNING
                banner = ""
                publish()
            }
            var last = SystemClock.elapsedRealtime()
            while (isActive && !finished) {
                delay(GameConfig.TICK_MS)
                val now = SystemClock.elapsedRealtime()
                val delta = (now - last).coerceIn(0L, MAX_FRAME_MS).toFloat() / MILLIS_PER_SECOND
                last = now
                tick(delta)
            }
        }
    }

    private fun tick(delta: Float) {
        if (phase == GamePhase.PAUSED || phase == GamePhase.CRASHED) {
            return
        }
        elapsedMs += (delta * MILLIS_PER_SECOND).toLong()

        if (phase == GamePhase.STUNNED && elapsedMs >= stunUntilMs) {
            phase = GamePhase.RUNNING
        }
        if (bannerUntilMs > 0L && elapsedMs >= bannerUntilMs) {
            banner = ""
            bannerUntilMs = 0L
        }

        val advanced = advanceRun(rows.toList(), delta, elapsedMs)
        rows.clear()
        rows.addAll(advanced)

        if (elapsedMs >= checkpointUntilMs) {
            cameraRow += scrollSpeed * delta / GameConfig.ROW_HEIGHT_DP
        }

        ensureRows()
        collectFeather()

        if (cameraRow > henRow) {
            crash(SampleData.CRASH_DUST)
            return
        }
        if (!resolveCollision()) {
            publish()
        }
    }

    private fun resolveCollision(): Boolean {
        val row = rowAt(henRow) ?: return false
        val hit = advanceRun.collisionAt(row, henLane) ?: return false
        if (hit == ObstacleKind.PUDDLE) {
            if (phase == GamePhase.RUNNING && henRow != stunnedRow) {
                stunnedRow = henRow
                phase = GamePhase.STUNNED
                stunUntilMs = elapsedMs + GameConfig.PUDDLE_STUN_MS
                publish()
            }
            return false
        }
        val reason = when (hit) {
            ObstacleKind.TRAIN -> SampleData.CRASH_TRAIN
            ObstacleKind.GATE -> SampleData.CRASH_GATE
            else -> SampleData.CRASH_WAGON
        }
        crash(reason)
        return true
    }

    private fun collectFeather() {
        val index = rows.indexOfFirst { it.index == henRow }
        if (index < 0) {
            return
        }
        val row = rows[index]
        if (row.hasFeather && row.featherLane == henLane) {
            rows[index] = row.copy(featherTaken = true)
            feathers += 1
        }
    }

    private fun rowAt(index: Int): RoadRow? = rows.firstOrNull { it.index == index }

    private fun ensureRows() {
        val target = henRow + GameConfig.ROWS_AHEAD
        var next = if (rows.isEmpty()) {
            GameConfig.START_CAMERA_ROW.toInt() - GameConfig.ROWS_BEHIND
        } else {
            rows[rows.size - 1].index + 1
        }
        while (next <= target) {
            rows.add(generateRoad(next, rows.toList(), SampleData.checkpointNames))
            next += 1
        }
        val lowest = (cameraRow - GameConfig.ROWS_BEHIND).toInt()
        while (rows.isNotEmpty() && rows[0].index < lowest) {
            rows.removeAt(0)
        }
    }

    private fun crash(reasonIndex: Int) {
        if (finished) {
            return
        }
        finished = true
        phase = GamePhase.CRASHED
        publish()
        viewModelScope.launch {
            delay(GameConfig.CRASH_SETTLE_MS)
            val previousBest = runRepository.bestDistance()
            val best = saveRunResult(distance, feathers, barns)
            val unlocked = unlockOutfit(runRepository.totalFeathers())
            _result.value = RunResult(
                distance = distance,
                feathers = feathers,
                barns = barns,
                bestDistance = best,
                newBest = distance > previousBest && distance > 0,
                unlockedOutfit = unlocked?.name.orEmpty(),
                crashLine = SampleData.crashLines[reasonIndex.coerceIn(0, SampleData.crashLines.size - 1)]
            )
        }
    }

    private fun publish() {
        _state.value = GameUiState(
            phase = phase,
            rows = rows.toList(),
            henRow = henRow,
            henLane = henLane,
            cameraRow = cameraRow,
            henScale = henScale(),
            bob = bobPhase(),
            distance = distance,
            feathers = feathers,
            barns = barns,
            banner = banner,
            stunned = phase == GamePhase.STUNNED,
            highContrast = highContrast,
            henTint = henTint
        )
    }

    private fun henScale(): Float {
        if (phase == GamePhase.CRASHED) {
            return CRASH_SCALE
        }
        if (elapsedMs >= hopUntilMs) {
            return 1f
        }
        val left = (hopUntilMs - elapsedMs).toFloat() / GameConfig.HOP_ANIM_MS
        val progress = (1f - left).coerceIn(0f, 1f)
        return 1f + HOP_STRETCH * sin(progress * Math.PI).toFloat()
    }

    private fun bobPhase(): Float =
        ((sin(elapsedMs / BOB_DIVISOR) + 1.0) / 2.0).toFloat()

    override fun onCleared() {
        super.onCleared()
        finished = true
        loopJob?.cancel()
        loopJob = null
    }

    companion object {
        private const val MAX_FRAME_MS = 64L
        private const val MILLIS_PER_SECOND = 1000f
        private const val CRASH_SCALE = 0.72f
        private const val HOP_STRETCH = 0.18f
        private const val BOB_DIVISOR = 300.0
    }
}
