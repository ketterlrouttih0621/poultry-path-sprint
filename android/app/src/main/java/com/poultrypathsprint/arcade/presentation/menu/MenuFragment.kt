package com.poultrypathsprint.arcade.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.navigation.Navigator
import com.poultrypathsprint.arcade.core.ui.ViewExtensions
import com.poultrypathsprint.arcade.databinding.FragmentMenuBinding
import com.poultrypathsprint.arcade.presentation.common.ViewModelFactory
import com.poultrypathsprint.arcade.presentation.dialog.HowToDialog
import com.poultrypathsprint.arcade.presentation.dialog.OutfitCoopDialog
import com.poultrypathsprint.arcade.presentation.dialog.SettingsDialog
import com.poultrypathsprint.arcade.presentation.game.GameFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val viewModel: MenuViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentMenuBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return

        val topMinimum = resources.getDimensionPixelSize(R.dimen.status_pad)
        ViewExtensions.applyTopInsetMargin(local.menuSettings, topMinimum)
        ViewExtensions.applyTopInsetMargin(local.menuFeatherBadge, topMinimum)
        ViewExtensions.applyBottomInsetPadding(local.menuSheetBody)

        local.menuPlay.setOnClickListener { openGame() }
        local.menuOutfits.setOnClickListener { showOutfits() }
        local.menuHowTo.setOnClickListener { showHowTo() }
        local.menuSettings.setOnClickListener { showSettings() }

        playEntrance(local)
        settleHen(local)
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    val local = _binding ?: return@collectLatest
                    local.menuFeatherCount.text = state.feathers.toString()
                    local.menuStatBest.bind(state.bestDistance, getString(R.string.label_best_metres))
                    local.menuStatFeathers.bind(state.feathers, getString(R.string.label_feathers))
                    val visible = listOf(local.menuStatBest, local.menuStatFeathers)
                        .count { it.visibility == View.VISIBLE }
                    local.menuStatsRow.visibility = if (visible >= 2) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun playEntrance(local: FragmentMenuBinding) {
        local.menuSheet.translationY = SHEET_SHIFT
        local.menuSheet.animate()
            .translationY(0f)
            .setDuration(SHEET_DURATION_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
        ViewExtensions.fadeIn(local.menuTitle, 0L)
        ViewExtensions.fadeIn(local.menuTagline, STAGGER_MS)
        ViewExtensions.fadeIn(local.menuStatsRow, STAGGER_MS * 2)
        ViewExtensions.fadeIn(local.menuPlayWrap, STAGGER_MS * 3)
        ViewExtensions.fadeIn(local.menuSecondaryRow, STAGGER_MS * 4)
    }

    private fun settleHen(local: FragmentMenuBinding) {
        local.menuHen.translationY = -BOB_SHIFT
        local.menuHen.animate()
            .translationY(0f)
            .setDuration(BOB_DURATION_MS)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun openGame() {
        if (!isAdded) {
            return
        }
        Navigator.replace(
            parentFragmentManager,
            GameFragment(),
            Navigator.TAG_GAME,
            true,
            Navigator.STYLE_SLIDE
        )
    }

    private fun showOutfits() {
        if (!isAdded) {
            return
        }
        OutfitCoopDialog().show(parentFragmentManager, OutfitCoopDialog.TAG)
    }

    private fun showHowTo() {
        if (!isAdded) {
            return
        }
        HowToDialog().show(parentFragmentManager, HowToDialog.TAG)
    }

    private fun showSettings() {
        if (!isAdded) {
            return
        }
        SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        val local = _binding
        if (local != null) {
            local.menuSheet.animate().cancel()
            local.menuHen.animate().cancel()
            local.menuTitle.animate().cancel()
            local.menuTagline.animate().cancel()
            local.menuStatsRow.animate().cancel()
            local.menuPlayWrap.animate().cancel()
            local.menuSecondaryRow.animate().cancel()
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val SHEET_SHIFT = 120f
        private const val SHEET_DURATION_MS = 380L
        private const val STAGGER_MS = 60L
        private const val BOB_SHIFT = 10f
        private const val BOB_DURATION_MS = 420L
    }
}
