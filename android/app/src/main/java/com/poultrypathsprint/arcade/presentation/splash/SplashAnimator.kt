package com.poultrypathsprint.arcade.presentation.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator

class SplashAnimator {

    private val animators = mutableListOf<ValueAnimator>()

    fun playEntrance(hen: View, titleTop: View, titleBottom: View, tagline: View, rule: View) {
        hen.alpha = 0f
        hen.scaleX = ENTRY_SCALE
        hen.scaleY = ENTRY_SCALE
        hen.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(ENTRY_DURATION_MS)
            .setInterpolator(OvershootInterpolator(OVERSHOOT_TENSION))
            .start()

        riseIn(titleTop, FIRST_DELAY_MS)
        riseIn(titleBottom, SECOND_DELAY_MS)
        riseIn(rule, THIRD_DELAY_MS)
        riseIn(tagline, FOURTH_DELAY_MS)
    }

    private fun riseIn(target: View, delayMs: Long) {
        target.alpha = 0f
        target.translationY = RISE_SHIFT
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delayMs)
            .setDuration(RISE_DURATION_MS)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun startLoops(hen: View, featherOne: View, featherTwo: View, featherThree: View) {
        stopLoops()
        animators.add(breathe(hen))
        animators.add(drift(featherOne, DRIFT_ONE_MS, DRIFT_SHIFT_ONE, ROTATION_ONE))
        animators.add(drift(featherTwo, DRIFT_TWO_MS, DRIFT_SHIFT_TWO, ROTATION_TWO))
        animators.add(drift(featherThree, DRIFT_THREE_MS, DRIFT_SHIFT_THREE, ROTATION_THREE))
        animators.add(spin(featherOne, SPIN_ONE_MS))
        animators.add(spin(featherThree, SPIN_TWO_MS))
        for (animator in animators) {
            animator.start()
        }
    }

    private fun breathe(target: View): ValueAnimator {
        val animator = ValueAnimator.ofFloat(1f, BREATHE_SCALE)
        animator.duration = BREATHE_DURATION_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { value ->
            val scale = value.animatedValue as Float
            target.scaleX = scale
            target.scaleY = scale
        }
        return animator
    }

    private fun drift(target: View, durationMs: Long, shift: Float, rotation: Float): ValueAnimator {
        val animator = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 0f, shift)
        animator.duration = durationMs
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        target.rotation = rotation
        return animator
    }

    private fun spin(target: View, durationMs: Long): ValueAnimator {
        val animator = ObjectAnimator.ofFloat(
            target,
            View.ROTATION,
            -ROTATION_SWING,
            ROTATION_SWING
        )
        animator.duration = durationMs
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
    }

    fun pulse(target: View) {
        val animator = ValueAnimator.ofFloat(PULSE_LOW, PULSE_HIGH)
        animator.duration = PULSE_DURATION_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            target.alpha = value.animatedValue as Float
        }
        animators.add(animator)
        animator.start()
    }

    fun stopLoops() {
        for (animator in animators) {
            animator.cancel()
        }
        animators.clear()
    }

    fun cancelAll(views: List<View>) {
        stopLoops()
        for (view in views) {
            view.animate().cancel()
        }
    }

    companion object {
        private const val ENTRY_SCALE = 0.72f
        private const val ENTRY_DURATION_MS = 520L
        private const val OVERSHOOT_TENSION = 1.6f
        private const val RISE_SHIFT = 26f
        private const val RISE_DURATION_MS = 420L
        private const val FIRST_DELAY_MS = 180L
        private const val SECOND_DELAY_MS = 280L
        private const val THIRD_DELAY_MS = 360L
        private const val FOURTH_DELAY_MS = 440L
        private const val BREATHE_SCALE = 1.05f
        private const val BREATHE_DURATION_MS = 1800L
        private const val DRIFT_ONE_MS = 2600L
        private const val DRIFT_TWO_MS = 3100L
        private const val DRIFT_THREE_MS = 3400L
        private const val DRIFT_SHIFT_ONE = -40f
        private const val DRIFT_SHIFT_TWO = -28f
        private const val DRIFT_SHIFT_THREE = -52f
        private const val ROTATION_ONE = -14f
        private const val ROTATION_TWO = 9f
        private const val ROTATION_THREE = 14f
        private const val ROTATION_SWING = 12f
        private const val SPIN_ONE_MS = 2900L
        private const val SPIN_TWO_MS = 3300L
        private const val PULSE_LOW = 0.55f
        private const val PULSE_HIGH = 1f
        private const val PULSE_DURATION_MS = 1200L
    }
}
