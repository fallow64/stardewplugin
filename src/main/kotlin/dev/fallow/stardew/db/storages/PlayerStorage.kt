package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.PlayerData
import dev.fallow.stardew.db.data.PlayerId
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*

object PlayerStorage : FolderStorageMap<PlayerId, PlayerData>(
    folder = File(StardewPlugin.main.dataFolder, "players"),
    keyTransformer = { it.id.toString() },
    keyComposer = { PlayerId(UUID.fromString(it)) },
    valueType = PlayerData::class.java
) {
    override fun emptyValue(key: PlayerId) = PlayerData(
        uniqueId = key
    )

    fun load(player: OfflinePlayer) = load(PlayerId(player.uniqueId))
}
