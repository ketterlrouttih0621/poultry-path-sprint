package com.poultrypathsprint.arcade.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.poultrypathsprint.arcade.core.navigation.Navigator
import com.poultrypathsprint.arcade.databinding.FragmentSplashBinding
import com.poultrypathsprint.arcade.presentation.common.ViewModelFactory
import com.poultrypathsprint.arcade.presentation.menu.MenuFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val viewModel: SplashViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val animator = SplashAnimator()
    private var handedOver = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentSplashBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return
        animator.playEntrance(
            local.splashHen,
            local.splashTitleTop,
            local.splashTitleBottom,
            local.splashTagline,
            local.splashRule
        )
        animator.startLoops(
            local.splashHen,
            local.splashFeatherOne,
            local.splashFeatherTwo,
            local.splashFeatherThree
        )
        animator.pulse(local.splashStatus)

        observeStatus()
        observeReady()
    }

    private fun observeStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statusStep.collectLatest { step ->
                    val local = _binding ?: return@collectLatest
                    local.splashStatus.setText(viewModel.statusResource(step))
                }
            }
        }
    }

    private fun observeReady() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ready.collectLatest { ready ->
                    if (ready) {
                        handOver()
                    }
                }
            }
        }
    }

    private fun handOver() {
        if (handedOver || !isAdded) {
            return
        }
        handedOver = true
        Navigator.replace(
            parentFragmentManager,
            MenuFragment(),
            Navigator.TAG_MENU,
            false,
            Navigator.STYLE_FADE
        )
    }

    override fun onDestroyView() {
        val local = _binding
        if (local != null) {
            animator.cancelAll(
                listOf(
                    local.splashHen,
                    local.splashTitleTop,
                    local.splashTitleBottom,
                    local.splashTagline,
                    local.splashRule,
                    local.splashStatus,
                    local.splashFeatherOne,
                    local.splashFeatherTwo,
                    local.splashFeatherThree
                )
            )
        } else {
            animator.stopLoops()
        }
        _binding = null
        super.onDestroyView()
    }
}
