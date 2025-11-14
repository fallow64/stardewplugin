package dev.fallow.stardew.util

object BytesHelper {
    /**
     * Formats the number of bytes into a human-readable format. Returns the number (as a string) and the associated character.
     */
    fun formatSize(v: Long): Pair<String, Char> {
        // https://stackoverflow.com/a/24805871

        if (v < 1024) return Pair(v.toString(), 'B')
        val z = (63 - v.countLeadingZeroBits()) / 10
        return Pair(
            String.format("%.1f", v.toDouble() / (1L shl (z * 10))),
            "KMGTPE"[z - 1]
        )
    }
}
