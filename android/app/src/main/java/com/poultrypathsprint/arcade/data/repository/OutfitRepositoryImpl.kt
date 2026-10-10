package com.poultrypathsprint.arcade.data.repository

import com.poultrypathsprint.arcade.data.local.PoultryPreferences
import com.poultrypathsprint.arcade.data.sample.SampleData
import com.poultrypathsprint.arcade.domain.model.Outfit
import com.poultrypathsprint.arcade.domain.repository.OutfitRepository

class OutfitRepositoryImpl(private val preferences: PoultryPreferences) : OutfitRepository {

    override fun outfits(): List<Outfit> = SampleData.outfits

    override fun unlockedIds(): Set<String> {
        val stored = preferences.readStringSet(
            PoultryPreferences.KEY_UNLOCKED_OUTFITS,
            setOf(SampleData.DEFAULT_OUTFIT_ID)
        )
        return stored + SampleData.DEFAULT_OUTFIT_ID
    }

    override fun equippedId(): String =
        preferences.readString(PoultryPreferences.KEY_EQUIPPED_OUTFIT, SampleData.DEFAULT_OUTFIT_ID)

    override fun unlock(id: String) {
        preferences.writeStringSet(PoultryPreferences.KEY_UNLOCKED_OUTFITS, unlockedIds() + id)
    }

    override fun equip(id: String) {
        if (unlockedIds().contains(id)) {
            preferences.writeString(PoultryPreferences.KEY_EQUIPPED_OUTFIT, id)
        }
    }
}
