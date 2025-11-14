package dev.fallow.stardew.db.data

import dev.fallow.stardew.util.FarmLocation3i
import org.bukkit.Material

@JvmInline
value class CropDefinitionId(val id: String)

/** The definition of a crop. Contains state such as id, models, seasons, etc. */
data class CropDefinition(
    val id: CropDefinitionId,
    val description: String,
    val material: Material,
)

/** An in-game crop which has been planted/grown. */
data class CropTile(
    val definition: CropDefinitionId,
    val location: FarmLocation3i,
    val timePlaced: Long,
    val currentDay: Int,
)
