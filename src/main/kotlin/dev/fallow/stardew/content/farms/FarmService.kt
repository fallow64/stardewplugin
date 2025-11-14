package dev.fallow.stardew.content.farms

import dev.fallow.stardew.services.StardewService
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object FarmService : StardewService(), Listener {
    override fun init() {
        Bukkit.getPluginManager().registerEvents(VanillaCropDisabler, plugin)
        Bukkit.getPluginManager().registerEvents(CustomCropListener, plugin)
    }
}
