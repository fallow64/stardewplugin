package dev.fallow.stardew.content.players

import dev.fallow.stardew.db.data.PlayerId
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.services.StardewService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object PlayerService : StardewService(), Listener {
    override fun init() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onPrePlayerJoin(e: AsyncPlayerPreLoginEvent) {
        // warning: this doesn't work within velocity
        if (e.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return

        // preload the data so first access doesn't lag
        val data = PlayerStorage.load(PlayerId(e.uniqueId))

        // also preload the farm, if they are in one
        val farmId = data.farmId
        if (farmId != null) {
            FarmStorage.load(farmId)
        }
    }
}
