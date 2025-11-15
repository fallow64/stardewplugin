package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.db.data.FarmId
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
}
