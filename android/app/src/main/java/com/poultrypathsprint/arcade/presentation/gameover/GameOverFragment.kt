package com.poultrypathsprint.arcade.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.navigation.Navigator
import com.poultrypathsprint.arcade.core.ui.ViewExtensions
import com.poultrypathsprint.arcade.databinding.FragmentGameOverBinding
import com.poultrypathsprint.arcade.domain.model.RunResult
import com.poultrypathsprint.arcade.presentation.common.ViewModelFactory
import com.poultrypathsprint.arcade.presentation.game.GameFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameOverBinding? = null
    private val viewModel: GameOverViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentGameOverBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return
        ViewExtensions.applyTopInset(local.resultBody, 0)
        ViewExtensions.applyBottomInsetPadding(local.resultBody)

        local.resultAgain.setOnClickListener { runAgain() }
        local.resultMenu.setOnClickListener { backToMenu() }

        val args = requireArguments()
        viewModel.load(
            args.getInt(ARG_DISTANCE, 0),
            args.getInt(ARG_FEATHERS, 0),
            args.getInt(ARG_BARNS, 0),
            args.getInt(ARG_BEST, 0),
            args.getBoolean(ARG_NEW_BEST, false),
            args.getString(ARG_UNLOCK).orEmpty(),
            args.getString(ARG_CRASH).orEmpty()
        )

        playEntrance(local)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    val local = _binding ?: return@collectLatest
                    local.resultVerdict.setText(
                        if (state.newBest) R.string.result_best else R.string.result_nice
                    )
                    local.resultReason.text = state.crashLine

                    local.resultStatMetres.bindAlways(
                        state.distance,
                        getString(R.string.label_metres)
                    )
                    local.resultStatFeathers.bind(state.feathers, getString(R.string.label_feathers))
                    local.resultStatBarns.bind(state.barns, getString(R.string.label_barns))

                    if (state.bestDistance > 0) {
                        local.resultBestPill.text =
                            getString(R.string.result_best_pill, state.bestDistance)
                        local.resultBestPill.visibility = View.VISIBLE
                    } else {
                        local.resultBestPill.visibility = View.GONE
                    }

                    if (state.unlockedOutfit.isNotEmpty()) {
                        local.resultUnlockText.text =
                            getString(R.string.result_unlock, state.unlockedOutfit.uppercase())
                        local.resultUnlockBanner.visibility = View.VISIBLE
                    } else {
                        local.resultUnlockBanner.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun playEntrance(local: FragmentGameOverBinding) {
        local.resultVerdict.scaleX = ENTRY_SCALE
        local.resultVerdict.scaleY = ENTRY_SCALE
        local.resultVerdict.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(ENTRY_DURATION_MS)
            .setInterpolator(OvershootInterpolator(OVERSHOOT_TENSION))
            .start()
        ViewExtensions.fadeIn(local.resultPanel, STAGGER_MS)
        ViewExtensions.fadeIn(local.resultAgainWrap, STAGGER_MS * 2)
        ViewExtensions.fadeIn(local.resultMenu, STAGGER_MS * 3)
    }

    private fun runAgain() {
        if (!isAdded) {
            return
        }
        Navigator.clearToMenu(parentFragmentManager)
        Navigator.replace(
            parentFragmentManager,
            GameFragment(),
            Navigator.TAG_GAME,
            true,
            Navigator.STYLE_SLIDE
        )
    }

    private fun backToMenu() {
        if (!isAdded) {
            return
        }
        Navigator.clearToMenu(parentFragmentManager)
    }

    override fun onDestroyView() {
        val local = _binding
        if (local != null) {
            local.resultVerdict.animate().cancel()
            local.resultPanel.animate().cancel()
            local.resultAgainWrap.animate().cancel()
            local.resultMenu.animate().cancel()
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_DISTANCE = "arg_distance"
        private const val ARG_FEATHERS = "arg_feathers"
        private const val ARG_BARNS = "arg_barns"
        private const val ARG_BEST = "arg_best"
        private const val ARG_NEW_BEST = "arg_new_best"
        private const val ARG_UNLOCK = "arg_unlock"
        private const val ARG_CRASH = "arg_crash"
        private const val ENTRY_SCALE = 0.8f
        private const val ENTRY_DURATION_MS = 420L
        private const val OVERSHOOT_TENSION = 2.2f
        private const val STAGGER_MS = 80L

        fun newInstance(result: RunResult): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putInt(ARG_DISTANCE, result.distance)
            args.putInt(ARG_FEATHERS, result.feathers)
            args.putInt(ARG_BARNS, result.barns)
            args.putInt(ARG_BEST, result.bestDistance)
            args.putBoolean(ARG_NEW_BEST, result.newBest)
            args.putString(ARG_UNLOCK, result.unlockedOutfit)
            args.putString(ARG_CRASH, result.crashLine)
            fragment.arguments = args
            return fragment
        }
    }
}
