package dev.fallow.stardew.db.data

import dev.fallow.stardew.content.farms.CropType
import dev.fallow.stardew.util.Location3i

data class CropTile(
    val cropType: CropType,
    val location: Location3i,
    val timePlaced: Long,
    val currentDay: Int,
)
