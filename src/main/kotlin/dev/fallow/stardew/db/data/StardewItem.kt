package dev.fallow.stardew.db.data

@JvmInline
value class StardewItemId(val id: String)

data class StardewItem(
    val id: StardewItemId
)