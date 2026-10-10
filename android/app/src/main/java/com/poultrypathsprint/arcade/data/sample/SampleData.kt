package com.poultrypathsprint.arcade.data.sample

import com.poultrypathsprint.arcade.domain.model.Outfit

object SampleData {

    const val DEFAULT_OUTFIT_ID = "farm_classic"

    val outfits: List<Outfit> = listOf(
        Outfit(
            id = DEFAULT_OUTFIT_ID,
            name = "Farm Classic",
            flavour = "Plain feathers, honest speed",
            cost = 0,
            accentColor = 0xFFF28C28.toInt()
        ),
        Outfit(
            id = "straw_hat",
            name = "Straw Hat",
            flavour = "Keeps the noon sun off the comb",
            cost = 40,
            accentColor = 0xFFF5D547.toInt()
        ),
        Outfit(
            id = "feather_boots",
            name = "Feather Boots",
            flavour = "Soft landings on the hardest dirt",
            cost = 90,
            accentColor = 0xFF3D9B7A.toInt()
        ),
        Outfit(
            id = "sunday_overalls",
            name = "Sunday Overalls",
            flavour = "Pressed denim for the market road",
            cost = 160,
            accentColor = 0xFF4B5A66.toInt()
        ),
        Outfit(
            id = "racing_scarf",
            name = "Racing Scarf",
            flavour = "Streams behind every single hop",
            cost = 240,
            accentColor = 0xFFD97B4D.toInt()
        ),
        Outfit(
            id = "golden_plume",
            name = "Golden Plume",
            flavour = "The pride of the whole hedgerow",
            cost = 400,
            accentColor = 0xFFDDB91F.toInt()
        )
    )

    val checkpointNames: List<String> = listOf(
        "Hedgerow Barn",
        "Millpond Barn",
        "Orchard Barn",
        "Windmill Barn",
        "Long Meadow Barn"
    )

    val crashLines: List<String> = listOf(
        "CAUGHT BY THE DUST",
        "WAGON WINS THIS ROUND",
        "THE TRAIN WAS FASTER",
        "THE GATE CLAPPED SHUT"
    )

    const val CRASH_DUST = 0
    const val CRASH_WAGON = 1
    const val CRASH_TRAIN = 2
    const val CRASH_GATE = 3
}
