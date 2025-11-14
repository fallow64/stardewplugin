package dev.fallow.stardew.util

import org.bukkit.Location
import java.util.*

data class FarmLocation3i(val farmId: UUID, val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "FarmLocation3i(${farmId},${x},${y},${z})"
    }
}

/** Converts a Bukkit location to a FarmLocation3i. Rounds down x, y, and z components. */
fun Location.toFarmLocation(farmId: UUID): FarmLocation3i {
    return FarmLocation3i(
        farmId = farmId,
        x = x.toInt(),
        y = y.toInt(),
        z = z.toInt()
    )
}
