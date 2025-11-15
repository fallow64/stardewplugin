package dev.fallow.stardew.content.farms

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent

object VanillaCropDisabler : Listener {
    /** Disable natural crop growth. */
    @EventHandler
    fun onBlockGrow(e: BlockGrowEvent) {
        if (isBlockCroplike(e.block.type)) e.isCancelled = true
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
        if (isBlockCroplike(e.toBlock.type)) e.isCancelled = true
    }

    /** Disable piston extensions that include any crop. */
    @EventHandler
    fun onPistonExtend(e: BlockPistonExtendEvent) {
        val anyIsCrop = e.blocks.any { isBlockCroplike(it.type) }
        if (anyIsCrop) {
            e.isCancelled = true
        }
    }

    // TODO: right now this works for just wheat, but if we want custom crops (i.e. via string) this logic needs to be changed
    // maybe instead of checking material, we know a farmId based on the region of the block
    // but for right now this works
    /** Returns whether a certain block type should be treated specially. */
    private fun isBlockCroplike(material: Material): Boolean {
        return when (material) {
            Material.WHEAT -> true
            else -> false
        }
    }
}