package dev.fallow.stardew.db.data

import dev.fallow.stardew.content.farms.CropType
import dev.fallow.stardew.util.FarmLocation3i

data class CropTile(
    val cropType: CropType,
    val location: FarmLocation3i,
    val timePlaced: Long,
    val currentDay: Int,
)
