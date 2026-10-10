package com.poultrypathsprint.arcade.domain.usecase

import com.poultrypathsprint.arcade.core.config.GameConfig
import com.poultrypathsprint.arcade.domain.model.Obstacle
import com.poultrypathsprint.arcade.domain.model.ObstacleKind
import com.poultrypathsprint.arcade.domain.model.RoadRow
import com.poultrypathsprint.arcade.domain.model.RowType
import kotlin.random.Random

class GenerateRoadUseCase(private val random: Random = Random.Default) {

    operator fun invoke(
        index: Int,
        previous: List<RoadRow>,
        checkpointNames: List<String>
    ): RoadRow {
        if (index <= GameConfig.SAFE_ZONE_LAST_ROW) {
            return RoadRow(index = index, type = RowType.GRASS)
        }
        if (index % GameConfig.BARN_EVERY_ROWS == 0) {
            val name = if (checkpointNames.isEmpty()) {
                ""
            } else {
                checkpointNames[(index / GameConfig.BARN_EVERY_ROWS - 1).mod(checkpointNames.size)]
            }
            return RoadRow(index = index, type = RowType.BARN, checkpointName = name)
        }
        return when (pickType(index, previous)) {
            RowType.DIRT_ROAD -> dirtRow(index)
            RowType.RAIL -> railRow(index)
            RowType.PUDDLE -> puddleRow(index)
            RowType.GATE -> gateRow(index)
            else -> grassRow(index)
        }
    }

    private fun pickType(index: Int, previous: List<RoadRow>): RowType {
        val tail = previous.takeLast(2)
        if (tail.size == 2 && tail.none { it.isSafeSurface }) {
            return RowType.GRASS
        }
        val weights = when {
            index <= EARLY_BAND -> EARLY_WEIGHTS
            index <= MID_BAND -> MID_WEIGHTS
            else -> LATE_WEIGHTS
        }
        var picked = weightedPick(weights)
        if (picked == RowType.RAIL && previous.lastOrNull()?.type == RowType.RAIL) {
            picked = RowType.DIRT_ROAD
        }
        return picked
    }

    private fun weightedPick(weights: List<Pair<RowType, Int>>): RowType {
        val total = weights.sumOf { it.second }
        if (total <= 0) {
            return RowType.GRASS
        }
        var roll = random.nextInt(total)
        for (entry in weights) {
            roll -= entry.second
            if (roll < 0) {
                return entry.first
            }
        }
        return RowType.GRASS
    }

    private fun grassRow(index: Int): RoadRow {
        val lane = if (random.nextFloat() < GameConfig.FEATHER_CHANCE) {
            random.nextInt(GameConfig.LANES)
        } else {
            -1
        }
        return RoadRow(index = index, type = RowType.GRASS, featherLane = lane)
    }

    private fun dirtRow(index: Int): RoadRow {
        val toRight = index % 2 == 0
        val count = if (index > MID_BAND && random.nextFloat() < 0.45f) 2 else 1
        val speedDp = GameConfig.WAGON_SPEED_MIN_DP +
            random.nextFloat() * (GameConfig.WAGON_SPEED_MAX_DP - GameConfig.WAGON_SPEED_MIN_DP)
        val speed = speedDp / GameConfig.LANE_REFERENCE_DP * if (toRight) 1f else -1f
        val wagons = (0 until count).map { slot ->
            val offset = if (toRight) {
                Obstacle.WRAP_LEFT - slot * 2.4f
            } else {
                Obstacle.WRAP_RIGHT + slot * 2.4f
            }
            Obstacle(
                kind = ObstacleKind.WAGON,
                position = offset + random.nextFloat() * 1.4f,
                width = WAGON_WIDTH,
                speed = speed
            )
        }
        return RoadRow(index = index, type = RowType.DIRT_ROAD, obstacles = wagons)
    }

    private fun railRow(index: Int): RoadRow {
        val toRight = index % 3 == 0
        val speed = GameConfig.TRAIN_SPEED_DP / GameConfig.LANE_REFERENCE_DP * if (toRight) 1f else -1f
        val start = if (toRight) {
            Obstacle.WRAP_LEFT - TRAIN_WIDTH - random.nextFloat() * 4f
        } else {
            Obstacle.WRAP_RIGHT + random.nextFloat() * 4f
        }
        val train = Obstacle(
            kind = ObstacleKind.TRAIN,
            position = start,
            width = TRAIN_WIDTH,
            speed = speed
        )
        return RoadRow(index = index, type = RowType.RAIL, obstacles = listOf(train))
    }

    private fun puddleRow(index: Int): RoadRow {
        val lane = random.nextInt(GameConfig.LANES)
        val puddle = Obstacle(
            kind = ObstacleKind.PUDDLE,
            position = lane + 0.1f,
            width = 0.8f,
            speed = 0f
        )
        return RoadRow(index = index, type = RowType.PUDDLE, obstacles = listOf(puddle))
    }

    private fun gateRow(index: Int): RoadRow {
        val gate = Obstacle(
            kind = ObstacleKind.GATE,
            position = 0f,
            width = GameConfig.LANES.toFloat(),
            speed = 0f
        )
        return RoadRow(index = index, type = RowType.GATE, obstacles = listOf(gate), gateOpen = true)
    }

    companion object {
        private const val EARLY_BAND = 20
        private const val MID_BAND = 60
        private const val WAGON_WIDTH = 1.05f
        private const val TRAIN_WIDTH = 1.9f

        private val EARLY_WEIGHTS = listOf(
            RowType.GRASS to 50,
            RowType.DIRT_ROAD to 40,
            RowType.PUDDLE to 10
        )
        private val MID_WEIGHTS = listOf(
            RowType.GRASS to 34,
            RowType.DIRT_ROAD to 40,
            RowType.RAIL to 12,
            RowType.PUDDLE to 8,
            RowType.GATE to 6
        )
        private val LATE_WEIGHTS = listOf(
            RowType.GRASS to 26,
            RowType.DIRT_ROAD to 38,
            RowType.RAIL to 18,
            RowType.PUDDLE to 8,
            RowType.GATE to 10
        )
    }
}
