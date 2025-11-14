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
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
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
        val old = FarmStorage.addCrop(newCropTile)
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
        val isDirectCrop = blockIsMinecraftCrop(e.block.type)
        val cropBlock = when {
            isDirectCrop -> e.block
            blockType == Material.FARMLAND && blockIsMinecraftCrop(blockUp.type) -> blockUp
            else -> return
        }

        // get the player's farm (if any) or return
        val farm = FarmStorage.loadPlayer(e.player) ?: return
        val crop = farm.getCrop(cropBlock.location) ?: return

        // at this point, we guarantee the player has destroyed a crop
        if (isDirectCrop) {
            // if they directly destroyed a crop, don't drop it
            e.isDropItems = false
        } else {
            // if it isn't directly a crop, set the above block to air to prevent item drop
            cropBlock.type = Material.AIR
        }

        // remove the crop
        FarmStorage.removeCrop(crop.location)
    }

    /** Handle watering can. */
    @EventHandler
    fun onInteraction(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val item = e.item ?: return
        val isWateringCan = Item.of(item).getTag("tool-type", DataType.STRING).getOrNull() == "watering-can"
        if (!isWateringCan) return

        val clickedBlock = e.clickedBlock ?: return
        
        // Check if clicking on farmland or a crop
        val targetBlock = when {
            clickedBlock.type == Material.FARMLAND -> clickedBlock
            blockIsMinecraftCrop(clickedBlock.type) -> clickedBlock.getRelative(BlockFace.DOWN)
            else -> return
        }

        if (targetBlock.type != Material.FARMLAND) return

        // Get the player's farm
        val farm = FarmStorage.loadPlayer(e.player) ?: return
        
        // Check if there's a crop on this farmland
        val cropLocation = targetBlock.getRelative(BlockFace.UP).location
        val crop = farm.getCrop(cropLocation) ?: return

        // Water the crop
        FarmStorage.waterCrop(crop.location)

        // Visual feedback: set farmland to moisturized
        val farmlandData = targetBlock.blockData as? org.bukkit.block.data.type.Farmland
        if (farmlandData != null) {
            farmlandData.moisture = farmlandData.maximumMoisture
            targetBlock.blockData = farmlandData
        }

        e.isCancelled = true
    }

    /** Returns whether a specific block is a Minecraft crop, i.e. wheat or carrot seeds (placed). */
    private fun blockIsMinecraftCrop(material: Material): Boolean {
        return when (material) {
            Material.WHEAT -> true
            else -> false
        }
    }
}
