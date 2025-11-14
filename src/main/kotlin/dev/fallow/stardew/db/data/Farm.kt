package dev.fallow.stardew.db.data

import dev.fallow.stardew.util.FarmLocation3i
import dev.fallow.stardew.util.toFarmLocation
import org.bukkit.Location
import java.util.*

@JvmInline
value class FarmId(val id: UUID)

/**
 * The data of a farm. Many players can join one farm. Exact Minecraft block data
 * is stored within worlds.
 */
data class Farm(
    val uniqueId: FarmId,
    // val worldId: UUID, // TODO: plots of land should have something here
    val players: MutableList<UUID>,
    val cropTiles: MutableMap<FarmLocation3i, CropTile>
) {
    fun getCrop(location: Location): CropTile? {
        return this.cropTiles[location.toFarmLocation(this.uniqueId)]
    }
}

