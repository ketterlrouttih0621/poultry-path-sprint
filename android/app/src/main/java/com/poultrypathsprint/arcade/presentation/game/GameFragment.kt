package com.poultrypathsprint.arcade.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.navigation.Navigator
import com.poultrypathsprint.arcade.core.ui.LaneBoardView
import com.poultrypathsprint.arcade.core.ui.ViewExtensions
import com.poultrypathsprint.arcade.databinding.FragmentGameBinding
import com.poultrypathsprint.arcade.domain.model.RunResult
import com.poultrypathsprint.arcade.presentation.common.ViewModelFactory
import com.poultrypathsprint.arcade.presentation.dialog.PauseDialog
import com.poultrypathsprint.arcade.presentation.gameover.GameOverFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameFragment : Fragment(), LaneBoardView.BoardListener, PauseDialog.PauseListener {

    private var _binding: FragmentGameBinding? = null
    private val viewModel: GameViewModel by viewModels { ViewModelFactory(requireContext()) }
    private var lastDistance = -1
    private var lastFeathers = -1
    private var lastBanner = ""
    private var handedOver = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentGameBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return

        ViewExtensions.applyTopInset(local.gameHeaderBody, 0)
        ViewExtensions.applyBottomInsetMargin(local.gameControls)

        local.gameBoard.listener = this
        local.gameHop.setOnClickListener { viewModel.onHop() }
        local.gameLaneLeft.setOnClickListener { viewModel.onLane(-1) }
        local.gameLaneRight.setOnClickListener { viewModel.onLane(1) }
        local.gamePause.setOnClickListener { showPause() }
        local.gameEmptyMenu.setOnClickListener { backToMenu() }

        observeState()
        observeResult()
    }

    override fun onResume() {
        super.onResume()
        if (handedOver) {
            backToMenu()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state -> render(state) }
            }
        }
    }

    private fun observeResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collectLatest { result ->
                    if (result != null) {
                        showResult(result)
                    }
                }
            }
        }
    }

    private fun render(state: GameUiState) {
        val local = _binding ?: return
        local.gameBoard.update(
            state.rows,
            state.henRow,
            state.henLane,
            state.cameraRow,
            state.henScale,
            state.bob,
            state.highContrast,
            state.henTint
        )
        if (state.distance != lastDistance) {
            lastDistance = state.distance
            local.gameDistance.text = state.distance.toString()
            ViewCompat.setStateDescription(
                local.gameHop,
                getString(R.string.state_hop, state.henLane + 1, state.distance)
            )
        }
        if (state.feathers != lastFeathers) {
            lastFeathers = state.feathers
            local.gameFeathers.text = state.feathers.toString()
        }

        val bannerText = when {
            state.phase == GamePhase.READY -> getString(R.string.game_get_set)
            state.stunned -> getString(R.string.announce_stun)
            state.banner.isNotEmpty() -> getString(R.string.game_checkpoint)
            else -> ""
        }
        if (bannerText != lastBanner) {
            lastBanner = bannerText
            local.gameBanner.text = bannerText
            local.gameBanner.visibility = if (bannerText.isEmpty()) View.GONE else View.VISIBLE
        }

        local.gameEmptyPanel.visibility = if (state.boardEmpty) View.VISIBLE else View.GONE
        ViewExtensions.setEnabledState(local.gameHop, state.controlsEnabled)
        ViewExtensions.setEnabledState(local.gameLaneLeft, state.controlsEnabled)
        ViewExtensions.setEnabledState(local.gameLaneRight, state.controlsEnabled)
    }

    private fun showPause() {
        if (!isAdded) {
            return
        }
        viewModel.pause()
        PauseDialog.newInstance(lastDistance.coerceAtLeast(0), lastFeathers.coerceAtLeast(0))
            .show(childFragmentManager, PauseDialog.TAG)
    }

    override fun onPauseResume() {
        viewModel.resumeRun()
    }

    override fun onPauseMenu() {
        backToMenu()
    }

    override fun onBoardHop() {
        viewModel.onHop()
    }

    override fun onBoardLane(delta: Int) {
        viewModel.onLane(delta)
    }

    private fun backToMenu() {
        if (!isAdded) {
            return
        }
        viewModel.abandon()
        Navigator.clearToMenu(parentFragmentManager)
    }

    private fun showResult(result: RunResult) {
        if (handedOver || !isAdded) {
            return
        }
        handedOver = true
        Navigator.replace(
            parentFragmentManager,
            GameOverFragment.newInstance(result),
            Navigator.TAG_RESULT,
            true,
            Navigator.STYLE_POP
        )
    }

    override fun onDestroyView() {
        val local = _binding
        if (local != null) {
            local.gameBoard.listener = null
        }
        _binding = null
        super.onDestroyView()
    }
}
