package dev.fallow.stardew.db.data

import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.util.Location3i
import dev.fallow.stardew.util.toLocation3i
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.util.*

data class Farm(
    val uniqueId: UUID,
    val players: MutableList<UUID>,
    val cropTiles: MutableMap<Location3i, CropTile>
) {
    companion object {
        /** Gets the farm of a player, or null. */
        fun getFarm(player: OfflinePlayer): Farm? {
            val playerData = PlayerStorage.load(player.uniqueId)
            val farmId = playerData.farmId ?: return null
            return FarmStorage.load(farmId)
        }

        /** Gets a farm by the farm's UUID. */
        fun getFarm(farmId: UUID): Farm? {
            return FarmStorage.load(farmId)
        }

        /** Returns whether a player is currently at the farm. */
        fun isPlayerWithinFarm(player: OfflinePlayer): Boolean {
            return PlayerStorage.load(player.uniqueId).inFarm
        }
    }

    fun getCrop(location: Location): CropTile? {
        return this.cropTiles[location.toLocation3i(this.uniqueId)]
    }

    /** Removes a crop within the storage. Does not handle destroying Minecraft blocks. */
    fun removeCrop(location: Location3i) {
        cropTiles.remove(location)
        FarmStorage.store(uniqueId, this)
    }
}

