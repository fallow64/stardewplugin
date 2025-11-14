package dev.fallow.stardew.db.storages

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.extra.FolderStorageMap
import dev.fallow.stardew.db.data.*
import java.io.File

object CropDefinitionStorage : FolderStorageMap<CropDefinitionId, CropDefinition?>(
    folder = File(StardewPlugin.main.dataFolder, "crops"),
    keyTransformer = { it.id },
    keyComposer = { CropDefinitionId(it) },
    valueType = run {
        // hack because nullish type isn't real
        @Suppress("UNCHECKED_CAST")
        CropDefinition::class.java as Class<CropDefinition?>
    },
) {
    override fun emptyValue(key: CropDefinitionId) = null
}
