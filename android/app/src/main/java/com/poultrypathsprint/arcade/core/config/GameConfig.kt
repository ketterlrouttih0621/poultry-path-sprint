package com.poultrypathsprint.arcade.core.config

object GameConfig {

    const val LOADER_DURATION_MS = 8000L
    const val SPLASH_STATUS_TWO_MS = 2600L
    const val SPLASH_STATUS_THREE_MS = 5200L

    const val TICK_MS = 16L
    const val LANES = 3
    const val ROW_HEIGHT_DP = 78f
    const val BOARD_FRAME_DP = 12f
    const val CONTROL_RESERVE_DP = 116f
    const val LANE_REFERENCE_DP = 110f

    const val SCROLL_DP_PER_SEC_START = 26f
    const val SCROLL_DP_PER_SEC_STEP = 1.6f
    const val SCROLL_DP_PER_SEC_MAX = 58f

    const val HOP_ANIM_MS = 180L
    const val LANE_ANIM_MS = 140L
    const val PUDDLE_STUN_MS = 420L
    const val GATE_CYCLE_MS = 2200L
    const val GATE_OPEN_MS = 1100L
    const val TRAIN_WARNING_MS = 1100L

    const val WAGON_SPEED_MIN_DP = 150f
    const val WAGON_SPEED_MAX_DP = 260f
    const val TRAIN_SPEED_DP = 720f

    const val BARN_EVERY_ROWS = 12
    const val FEATHER_CHANCE = 0.28f
    const val CRASH_SETTLE_MS = 900L
    const val READY_HOLD_MS = 600L
    const val CHECKPOINT_HOLD_MS = 900L
    const val BANNER_VISIBLE_MS = 1200L
    const val SWIPE_THRESHOLD_DP = 48f

    const val START_ROW = 1
    const val START_CAMERA_ROW = -3f
    const val SAFE_ZONE_LAST_ROW = 2
    const val ROWS_AHEAD = 16
    const val ROWS_BEHIND = 4
}
