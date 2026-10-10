package com.poultrypathsprint.arcade.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.poultrypathsprint.arcade.R

object Navigator {

    const val TAG_SPLASH = "splash"
    const val TAG_MENU = "menu"
    const val TAG_GAME = "game"
    const val TAG_RESULT = "result"

    const val STYLE_FADE = 0
    const val STYLE_SLIDE = 1
    const val STYLE_POP = 2

    fun showSplash(manager: FragmentManager, fragment: Fragment) {
        if (manager.isStateSaved) {
            return
        }
        manager.beginTransaction()
            .replace(R.id.fragment_container, fragment, TAG_SPLASH)
            .commit()
    }

    fun replace(
        manager: FragmentManager,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean,
        style: Int
    ) {
        if (manager.isStateSaved) {
            return
        }
        val transaction = manager.beginTransaction()
        when (style) {
            STYLE_SLIDE -> transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            STYLE_POP -> transaction.setCustomAnimations(
                R.anim.scale_fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            else -> transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
        }
        transaction.replace(R.id.fragment_container, fragment, tag)
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun clearToMenu(manager: FragmentManager) {
        if (manager.isStateSaved) {
            return
        }
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
