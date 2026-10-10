package com.poultrypathsprint.arcade.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStatCardBinding =
        ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accent: Int = Color.parseColor(DEFAULT_ACCENT)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.clay_card)
        val pad = resources.getDimensionPixelSize(R.dimen.gap_s)
        setPadding(pad, pad, pad, pad + resources.getDimensionPixelSize(R.dimen.gap_xs))
        minimumHeight = resources.getDimensionPixelSize(R.dimen.outfit_row_height)

        if (attrs != null) {
            val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
            val value = typed.getString(R.styleable.StatCardView_appValue)
            val label = typed.getString(R.styleable.StatCardView_appLabel)
            accent = typed.getColor(R.styleable.StatCardView_appAccent, accent)
            typed.recycle()
            if (value != null) {
                binding.statValue.text = value
            }
            if (label != null) {
                binding.statLabel.text = label
            }
        }
        applyAccent()
    }

    fun setValue(value: String) {
        binding.statValue.text = value
        ViewCompat.setStateDescription(this, value)
    }

    fun setLabel(label: String) {
        binding.statLabel.text = label
    }

    fun setAccent(color: Int) {
        accent = color
        applyAccent()
    }

    fun bind(value: Int, label: String) {
        setValue(value.toString())
        setLabel(label)
        visibility = if (value > 0) View.VISIBLE else View.GONE
    }

    fun bindAlways(value: Int, label: String) {
        setValue(value.toString())
        setLabel(label)
        visibility = View.VISIBLE
    }

    private fun applyAccent() {
        binding.statDot.backgroundTintList = ColorStateList.valueOf(accent)
        binding.statValue.setTextColor(accent)
    }

    companion object {
        private const val DEFAULT_ACCENT = "#F28C28"
    }
}
