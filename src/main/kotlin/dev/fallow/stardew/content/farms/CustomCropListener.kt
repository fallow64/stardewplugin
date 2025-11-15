package dev.fallow.stardew.content.farms

import com.marcusslover.plus.lib.common.DataType
import com.marcusslover.plus.lib.item.Item
import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.data.CropDefinitionId
import dev.fallow.stardew.db.data.CropTile
import dev.fallow.stardew.db.storages.CropDefinitionStorage
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.util.toFarmLocation
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import kotlin.jvm.optionals.getOrNull

object CustomCropListener : Listener {
    /** Handle crop placement */
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        // test, for now give player example seed if they place any block
        e.player.give(Item.of(Material.WHEAT_SEEDS).setTag("crop-type", "parsnip", DataType.STRING).itemStack())

        // get the cropType from a custom tag
        val rawStringTag = Item.of(e.itemInHand.asOne()).getTag("crop-type", DataType.STRING).getOrNull() ?: return
        val cropDefinitionId = CropDefinitionId(rawStringTag)

        // load the crop definition to get the block material
        val cropDefinition = CropDefinitionStorage.load(cropDefinitionId)
        if (cropDefinition == null) {
            StardewPlugin.logger.warning("Crop definition not found for id: ${cropDefinitionId.id}")
            e.isCancelled = true
            return
        }

        // player must have farm and currently be within their farm to place a custom crop
        // TODO: check if they're within their farm or in common area
        val playerData = PlayerStorage.load(e.player)
        val playerFarmId = playerData.farmId ?: run {
            e.isCancelled = true
            return
        }

        // set the block to the crop definition's material
        e.block.type = cropDefinition.material

        // create the new crops
        val location = e.block.location.toFarmLocation(playerFarmId)
        val newCropTile = CropTile(
            definition = cropDefinitionId,
            location = location,
        )

        // add it to the farm
        val farm = FarmStorage.load(playerFarmId) ?: error("Invalid playerFarmId")
        val old = farm.addCrop(newCropTile)

        // check if we overwrite something
        if (old != null) {
            StardewPlugin.logger.severe("Crop has been overwritten by block place. Location: $location.")
        }
    }

    /** Handle crop breaking. */
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val blockType = e.block.type
        val blockUp = e.block.getRelative(BlockFace.UP)

        // return if crop isn't destroyed (or get data about that crop)
        val isFarmland = blockType == Material.FARMLAND
        val cropBlock = if (isFarmland) {
            e.block
        } else {
            blockUp
        }

        // get the player's farm (if any) or return
        val farm = FarmStorage.loadPlayer(e.player) ?: return
        val crop = farm.getCrop(cropBlock.location) ?: return

        // at this point, we guarantee the player has destroyed a crop
        if (isFarmland) {
            // if we're breaking farmland that leads to breaking a crop, set it as air
            // TODO: this is a bit janky? weird how there's two things
            cropBlock.type = Material.AIR
        } else {
            // otherwise just set drop items to false like normal
            e.isDropItems = false
        }

        // remove the crop
        farm.removeCrop(crop.location)
    }
}
