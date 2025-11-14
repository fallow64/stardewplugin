package dev.fallow.stardew.db.data

import java.util.*

@JvmInline
value class PlayerId(val id: UUID)

/**
 * The exact data associated with any player. This should broadly represent long-term
 * storage associated with the player. Prefer a local Map<UUID, Value> for non-persistent data.
 */
data class PlayerData(
    val uniqueId: PlayerId,
    var farmId: FarmId?,
)
