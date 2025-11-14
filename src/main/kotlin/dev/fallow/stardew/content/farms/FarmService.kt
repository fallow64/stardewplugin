package dev.fallow.stardew.content.farms

import com.marcusslover.plus.lib.common.DataType
import com.marcusslover.plus.lib.item.Item
import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.data.CropTile
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.services.StardewService
import dev.fallow.stardew.util.Location3i
import dev.fallow.stardew.util.toLocation3i
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.MoistureChangeEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull

object FarmService : StardewService(), Listener {
    override fun init() {
        Bukkit.getPluginManager().registerEvents(CropChangeListener, plugin)
    }
}