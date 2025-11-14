package dev.fallow.stardew.content.farms

import com.marcusslover.plus.lib.common.DataType
import com.marcusslover.plus.lib.item.Item
import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.data.CropTile
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.util.toLocation3i
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.MoistureChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.jvm.optionals.getOrNull

object CropChangeListener : Listener {
    /** Handle crop placement */
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        // test, for now give player example seed if they place any block
        e.player.give(Item.of(Material.WHEAT_SEEDS).setTag("crop-type", "parsnip", DataType.STRING).itemStack())

        // get the cropType from a custom tag
        val cropTypeTag = Item.of(e.itemInHand.asOne()).getTag("crop-type", DataType.STRING).getOrNull() ?: return
        val cropType = CropType.entries.firstOrNull { it.id == cropTypeTag }
        if (cropType == null) {
            StardewPlugin.logger.warning("Invalid 'crop-type' tag: $cropTypeTag")
            return
        }

        val farm = Farm.getFarm(e.player) ?: run {
            e.isCancelled = true
            return
        }

        val location = e.block.location.toLocation3i(farm.uniqueId)
        val cropTile = CropTile(
            cropType = cropType,
            location = location,
            timePlaced = System.currentTimeMillis(),
            currentDay = 0,
        )

        // add it to the farm
        FarmStorage.write(farm.uniqueId) {
            requireNotNull(it) { "farm should be initialized by here" }

            val old = it.cropTiles.put(location, cropTile)
            if (old != null) {
                StardewPlugin.logger.severe("Crop has been overwritten by block place. Location: $location.")
            }
        }
    }

    /** Handle crop breaking. */
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val blockType = e.block.type
        val blockUp = e.block.getRelative(BlockFace.UP)

        // due to this when, early return when crop is not destroyed
        val isDirectCrop = blockIsMinecraftCrop(e.block.type)
        val cropBlock = when {
            isDirectCrop -> e.block
            blockType == Material.FARMLAND && blockIsMinecraftCrop(blockUp.type) -> blockUp
            else -> return
        }

        // get the player's farm (if any) or return
        val farm = Farm.getFarm(e.player) ?: return
        val crop = farm.getCrop(cropBlock.location) ?: return

        // at this point, we guarantee the player has destroyed a crop
        if (isDirectCrop) {
            // if they directly destroyed a crop, don't drop it
            e.isDropItems = false
        } else {
            // if it isn't directly a crop, set the above block to air
            cropBlock.type = Material.AIR
        }

        // remove the crop
        farm.removeCrop(crop.location)
    }

    /** Disable natural crop growth. */
    @EventHandler
    fun onBlockGrow(e: BlockGrowEvent) {
        if (blockIsMinecraftCrop(e.block.type)) e.isCancelled = true
    }

    /** Disable farmland drying out. */
    @EventHandler
    fun onBlockFade(e: BlockFadeEvent) {
        if (e.block.type == Material.FARMLAND) e.isCancelled = true
    }

    /** Disable player trample farmland. */
    @EventHandler
    fun onInteraction(e: PlayerInteractEvent) {
        if (e.action == Action.PHYSICAL && e.clickedBlock?.type == Material.FARMLAND) {
            e.isCancelled = true
        }
    }

    /** Disable automatic farmland water propagation. */
    @EventHandler
    fun onMoistureChange(e: MoistureChangeEvent) {
        e.isCancelled = true
    }

    /** Disable water flowing over crops. */
    @EventHandler
    fun onBlockMove(e: BlockFromToEvent) {
        if (blockIsMinecraftCrop(e.toBlock.type)) e.isCancelled = true
    }

    /** Disable piston extensions that include any crop. */
    @EventHandler
    fun onPistonExtend(e: BlockPistonExtendEvent) {
        val anyIsCrop = e.blocks.any { blockIsMinecraftCrop(it.type) }
        if (anyIsCrop) {
            e.isCancelled = true
        }
    }

    /** Returns whether a specific block is a Minecraft crop, i.e. wheat or carrot seeds (placed). */
    private fun blockIsMinecraftCrop(material: Material): Boolean {
        return when (material) {
            Material.WHEAT -> true
            else -> false
        }
    }
}