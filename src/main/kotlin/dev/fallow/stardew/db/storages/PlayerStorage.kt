package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.PlayerData
import java.io.File
import java.util.*

object PlayerStorage : FolderStorageMap<UUID, PlayerData>(
    folder = File(StardewPlugin.main.dataFolder, "players"),
    keyTransformer = UUID::toString,
    keyComposer = UUID::fromString,
    valueType = PlayerData::class.java
) {
    override fun emptyValue(key: UUID) = PlayerData(
        uniqueId = key,
        farmId = null,
        inFarm = false,
    )
}
