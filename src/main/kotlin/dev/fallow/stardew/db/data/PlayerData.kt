package dev.fallow.stardew.db.data

import java.util.*

@JvmInline
value class PlayerId(val id: UUID)

data class PlayerData(
    val uniqueId: PlayerId,
    var farmId: FarmId? = null
)
