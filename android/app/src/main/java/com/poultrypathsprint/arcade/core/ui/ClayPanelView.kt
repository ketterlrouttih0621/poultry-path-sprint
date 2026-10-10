package com.poultrypathsprint.arcade.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.databinding.ViewClayPanelBinding

class ClayPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewClayPanelBinding =
        ViewClayPanelBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.clay_dialog_panel)
        var pad = resources.getDimensionPixelSize(R.dimen.dialog_panel_padding)
        var tint = 0

        if (attrs != null) {
            val typed = context.obtainStyledAttributes(attrs, R.styleable.ClayPanelView)
            val title = typed.getString(R.styleable.ClayPanelView_appPanelTitle)
            tint = typed.getColor(R.styleable.ClayPanelView_appPanelTint, 0)
            pad = typed.getDimensionPixelSize(R.styleable.ClayPanelView_appPanelPadding, pad)
            typed.recycle()
            if (title != null) {
                setTitle(title)
            }
        }
        setPadding(pad, pad, pad, pad)
        if (tint != 0) {
            backgroundTintList = ColorStateList.valueOf(tint)
        }
    }

    fun setTitle(title: String) {
        binding.panelTitle.text = title
        binding.panelTitle.visibility = View.VISIBLE
    }

    fun hideTitle() {
        binding.panelTitle.visibility = View.GONE
    }
}
