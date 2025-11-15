package dev.fallow.stardew.db.data

import dev.fallow.stardew.util.FarmLocation3i
import dev.fallow.stardew.util.toFarmLocation
import org.bukkit.Location
import java.util.*

@JvmInline
value class FarmId(val id: UUID)

/** An in-game crop which has been planted/grown. */
data class CropTile(
    val definition: CropDefinitionId,
    val location: FarmLocation3i,
    val timePlaced: Long = System.currentTimeMillis(),
    val currentDay: Int = 0,
    @Transient
    var watered: Boolean = false,
)

/** The main class for a farm, which may have multiple players. */
data class Farm(
    val uniqueId: FarmId,
    // val worldId: UUID, // TODO: plots of land should have something here
    private val players: MutableList<PlayerId>,
    private val cropTiles: MutableMap<FarmLocation3i, CropTile>
) {
    /** Get a crop at a specific location. Rounds the crop to the nearest integer block. */
    fun getCrop(location: Location): CropTile? {
        return this.cropTiles[location.toFarmLocation(this.uniqueId)]
    }

    /** Adds a crop to a farm. Returns the old crop tile, if any (which should be problematic). */
    fun addCrop(crop: CropTile): CropTile? {
        return cropTiles.put(crop.location, crop)
    }

    /** Removes a crop at a given location. Returns the removed crop, if any. */
    fun removeCrop(location: FarmLocation3i): CropTile? {
        return cropTiles.remove(location)
    }

    /** Adds a player to the farm. */
    fun addPlayer(playerId: PlayerId) {
        players.add(playerId)
    }

    /** Removes a player from the farm. */
    fun removePlayer(playerId: PlayerId) {
        players.remove(playerId)
    }
}
