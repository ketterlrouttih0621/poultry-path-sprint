package com.poultrypathsprint.arcade.domain.usecase

import com.poultrypathsprint.arcade.core.config.GameConfig
import com.poultrypathsprint.arcade.domain.model.Obstacle
import com.poultrypathsprint.arcade.domain.model.ObstacleKind
import com.poultrypathsprint.arcade.domain.model.RoadRow
import com.poultrypathsprint.arcade.domain.model.RowType

class AdvanceRunUseCase {

    operator fun invoke(rows: List<RoadRow>, deltaSeconds: Float, elapsedMs: Long): List<RoadRow> {
        return rows.map { row ->
            when (row.type) {
                RowType.GATE -> row.copy(gateOpen = gateOpen(row.index, elapsedMs))
                RowType.RAIL -> {
                    val moved = row.obstacles.map { it.movedBy(deltaSeconds) }
                    row.copy(obstacles = moved, warning = warning(moved))
                }
                RowType.DIRT_ROAD -> row.copy(obstacles = row.obstacles.map { it.movedBy(deltaSeconds) })
                else -> row
            }
        }
    }

    fun collisionAt(row: RoadRow, lane: Int): ObstacleKind? {
        val from = lane + HITBOX_INSET
        val to = lane + 1f - HITBOX_INSET
        for (obstacle in row.obstacles) {
            if (obstacle.kind == ObstacleKind.GATE && row.gateOpen) {
                continue
            }
            if (obstacle.end > from && obstacle.start < to) {
                return obstacle.kind
            }
        }
        return null
    }

    fun gateOpen(rowIndex: Int, elapsedMs: Long): Boolean {
        val phase = (elapsedMs + rowIndex * GATE_PHASE_SHIFT_MS).mod(GameConfig.GATE_CYCLE_MS)
        return phase < GameConfig.GATE_OPEN_MS
    }

    private fun warning(obstacles: List<Obstacle>): Boolean {
        for (obstacle in obstacles) {
            if (obstacle.speed > 0f && obstacle.end < WARNING_EDGE_LEFT) {
                return true
            }
            if (obstacle.speed < 0f && obstacle.start > WARNING_EDGE_RIGHT) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val HITBOX_INSET = 0.2f
        private const val GATE_PHASE_SHIFT_MS = 260L
        private const val WARNING_EDGE_LEFT = -0.1f
        private const val WARNING_EDGE_RIGHT = 3.1f
    }
}
