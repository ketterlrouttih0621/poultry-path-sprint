package com.poultrypathsprint.arcade.domain.model

data class Obstacle(
    val kind: ObstacleKind,
    val position: Float,
    val width: Float,
    val speed: Float
) {
    val start: Float
        get() = position

    val end: Float
        get() = position + width

    fun movedBy(deltaSeconds: Float): Obstacle {
        if (speed == 0f) {
            return this
        }
        var next = position + speed * deltaSeconds
        if (speed > 0f && next > WRAP_RIGHT) {
            next = WRAP_LEFT - width
        }
        if (speed < 0f && next + width < WRAP_LEFT) {
            next = WRAP_RIGHT
        }
        return copy(position = next)
    }

    companion object {
        const val WRAP_LEFT = -1.6f
        const val WRAP_RIGHT = 4.6f
    }
}
