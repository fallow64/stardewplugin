package dev.fallow.stardew.db.data

import java.util.*

data class PlayerData(
    val uniqueId: UUID,
    var farmId: UUID?,
    var inFarm: Boolean,
)