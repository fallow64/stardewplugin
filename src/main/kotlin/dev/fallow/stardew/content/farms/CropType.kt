package dev.fallow.stardew.content.farms

import com.google.gson.annotations.SerializedName
import org.bukkit.Material

enum class CropType(
    // TODO: reference custom item directory, reference custom seeds directly
    // right now just name and material
    val id: String,
    val description: String,
    val material: Material,
    val seasonsToGrowIn: List<CropSeason>,
) {
    @SerializedName("parsnip")
    Parsnip(
        id = "parsnip",
        description = "a",
        material = Material.WHEAT_SEEDS,
        seasonsToGrowIn = listOf(CropSeason.Spring)
    )
}
