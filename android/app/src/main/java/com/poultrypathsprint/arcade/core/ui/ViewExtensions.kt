package com.poultrypathsprint.arcade.core.ui

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

object ViewExtensions {

    fun applyTopInset(target: View, extra: Int) {
        val base = target.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(target) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updatePadding(top = maxOf(base, top + extra))
            insets
        }
        ViewCompat.requestApplyInsets(target)
    }

    fun applyTopInsetMargin(target: View, minimumPx: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(target) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = maxOf(minimumPx, top)
            }
            insets
        }
        ViewCompat.requestApplyInsets(target)
    }

    fun applyBottomInsetMargin(target: View) {
        val params = target.layoutParams
        val base = if (params is ViewGroup.MarginLayoutParams) params.bottomMargin else 0
        ViewCompat.setOnApplyWindowInsetsListener(target) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = base + bottom
            }
            insets
        }
        ViewCompat.requestApplyInsets(target)
    }

    fun applyBottomInsetPadding(target: View) {
        val base = target.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(target) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = base + bottom)
            insets
        }
        ViewCompat.requestApplyInsets(target)
    }

    fun fadeIn(target: View, delayMs: Long) {
        target.alpha = 0f
        target.translationY = FADE_SHIFT
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delayMs)
            .setDuration(FADE_DURATION_MS)
            .start()
    }

    fun setEnabledState(target: View, enabled: Boolean) {
        target.isEnabled = enabled
        target.alpha = if (enabled) 1f else DISABLED_ALPHA
    }

    private const val FADE_SHIFT = 18f
    private const val FADE_DURATION_MS = 260L
    private const val DISABLED_ALPHA = 0.45f
}
