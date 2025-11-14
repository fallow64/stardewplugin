package dev.fallow.stardew.util

import org.bukkit.Location
import java.util.*

fun Location.toLocation3i(farmId: UUID): Location3i {
    return Location3i(
        farmId = farmId,
        x = x.toInt(),
        y = y.toInt(),
        z = z.toInt()
    )
}

data class Location3i(val farmId: UUID, val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "Location3i(${farmId},${x},${y},${z})"
    }
}
