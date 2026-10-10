package com.poultrypathsprint.arcade.domain.model

data class RoadRow(
    val index: Int,
    val type: RowType,
    val obstacles: List<Obstacle> = emptyList(),
    val featherLane: Int = -1,
    val featherTaken: Boolean = false,
    val warning: Boolean = false,
    val gateOpen: Boolean = true,
    val checkpointName: String = ""
) {
    val hasFeather: Boolean
        get() = featherLane >= 0 && !featherTaken

    val isSafeSurface: Boolean
        get() = type == RowType.GRASS || type == RowType.BARN
}
