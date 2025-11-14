package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.CropTile
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.db.data.FarmId
import dev.fallow.stardew.util.FarmLocation3i
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*

object FarmStorage : FolderStorageMap<FarmId, Farm?>(
    folder = File(StardewPlugin.main.dataFolder, "farms"),
    keyTransformer = { it.id.toString() },
    keyComposer = { FarmId(UUID.fromString(it)) },
    valueType = run {
        // hack because nullish type isn't real
        @Suppress("UNCHECKED_CAST")
        Farm::class.java as Class<Farm?>
    },
) {
    override fun emptyValue(key: FarmId) = null

    fun loadPlayer(player: OfflinePlayer): Farm? {
        val farmId = PlayerStorage.load(player).farmId ?: return null
        return load(farmId)
    }

    fun addCrop(crop: CropTile): CropTile? {
        var old: CropTile? = null
        FarmStorage.write(crop.location.farmId) {
            requireNotNull(it) { "Attempt to place crop where farmId not found" }
            old = it.cropTiles.put(crop.location, crop)
        }
        return old
    }

    fun removeCrop(location: FarmLocation3i): CropTile? {
        var old: CropTile? = null
        FarmStorage.write(location.farmId) {
            requireNotNull(it) { "Attempt to remove crop where farmId not found" }
            old = it.cropTiles.remove(location)
        }
        return old
    }

    fun waterCrop(location: FarmLocation3i): Boolean {
        var success = false
        FarmStorage.write(location.farmId) {
            requireNotNull(it) { "Attempt to water crop where farmId not found" }
            val crop = it.cropTiles[location]
            if (crop != null) {
                crop.watered = true
                success = true
            }
        }
        return success
    }
}
