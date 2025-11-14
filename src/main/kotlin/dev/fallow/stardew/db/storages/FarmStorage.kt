package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.Farm
import java.io.File
import java.util.*

object FarmStorage : FolderStorageMap<UUID, Farm?>(
    folder = File(StardewPlugin.main.dataFolder, "farms"),
    keyTransformer = UUID::toString,
    keyComposer = UUID::fromString,
    valueType = run {
        // hack because nullish type isn't real
        @Suppress("UNCHECKED_CAST")
        Farm::class.java as Class<Farm?>
    },
) {
    override fun emptyValue(key: UUID) = null
}
