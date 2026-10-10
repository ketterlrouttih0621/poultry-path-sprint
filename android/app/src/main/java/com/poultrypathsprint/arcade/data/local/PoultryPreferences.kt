package com.poultrypathsprint.arcade.data.local

import android.content.Context
import android.content.SharedPreferences

class PoultryPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readStringSet(key: String, fallback: Set<String>): Set<String> =
        prefs.getStringSet(key, fallback) ?: fallback

    fun writeStringSet(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    fun clearProgress() {
        prefs.edit()
            .remove(KEY_BEST_DISTANCE)
            .remove(KEY_FEATHERS_TOTAL)
            .remove(KEY_BARNS_TOTAL)
            .remove(KEY_RUNS_TOTAL)
            .remove(KEY_UNLOCKED_OUTFITS)
            .remove(KEY_EQUIPPED_OUTFIT)
            .apply()
    }

    companion object {
        private const val STORE = "poultry_path_sprint_store"
        const val KEY_BEST_DISTANCE = "best_distance"
        const val KEY_FEATHERS_TOTAL = "feathers_total"
        const val KEY_BARNS_TOTAL = "barns_total"
        const val KEY_RUNS_TOTAL = "runs_total"
        const val KEY_EQUIPPED_OUTFIT = "equipped_outfit_id"
        const val KEY_UNLOCKED_OUTFITS = "unlocked_outfit_ids"
        const val KEY_SOUND = "sound_enabled"
        const val KEY_SHAKE = "shake_enabled"
        const val KEY_CONTRAST = "high_contrast_enabled"
        const val KEY_SENSITIVITY = "hop_sensitivity"
    }
}
