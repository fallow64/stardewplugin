package dev.fallow.stardew.db.data

import dev.fallow.stardew.util.FarmLocation3i
import dev.fallow.stardew.util.toFarmLocation
import org.bukkit.Location
import java.util.*

data class Farm(
    val uniqueId: UUID,
    val players: MutableList<UUID>,
    val cropTiles: MutableMap<FarmLocation3i, CropTile>
) {
    fun getCrop(location: Location): CropTile? {
        return this.cropTiles[location.toFarmLocation(this.uniqueId)]
    }
}

