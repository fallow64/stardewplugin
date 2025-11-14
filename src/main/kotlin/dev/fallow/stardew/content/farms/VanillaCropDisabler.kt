package dev.fallow.stardew.content.farms

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.MoistureChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

object VanillaCropDisabler : Listener {
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