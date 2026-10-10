package com.poultrypathsprint.arcade.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.poultrypathsprint.arcade.core.di.ServiceLocator
import com.poultrypathsprint.arcade.presentation.game.GameViewModel
import com.poultrypathsprint.arcade.presentation.gameover.GameOverViewModel
import com.poultrypathsprint.arcade.presentation.menu.MenuViewModel
import com.poultrypathsprint.arcade.presentation.splash.SplashViewModel

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appContext: Context = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.runRepository(appContext),
                ServiceLocator.outfitRepository(appContext)
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.generateRoadUseCase(),
                ServiceLocator.advanceRunUseCase(),
                ServiceLocator.saveRunResultUseCase(appContext),
                ServiceLocator.unlockOutfitUseCase(appContext),
                ServiceLocator.runRepository(appContext),
                ServiceLocator.outfitRepository(appContext),
                ServiceLocator.settingsRepository(appContext)
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.runRepository(appContext)
            )
            else -> throw IllegalArgumentException(UNKNOWN + modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }

    companion object {
        private const val UNKNOWN = "Unsupported ViewModel: "
    }
}
