package com.poultrypathsprint.arcade.domain.repository

import com.poultrypathsprint.arcade.domain.model.Outfit

interface OutfitRepository {
    fun outfits(): List<Outfit>
    fun unlockedIds(): Set<String>
    fun equippedId(): String
    fun unlock(id: String)
    fun equip(id: String)
}
