package dev.fallow.stardew

import dev.fallow.stardew.content.farms.FarmService
import dev.fallow.stardew.content.players.PlayerService
import dev.fallow.stardew.db.StorageService
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.services.PlusService
import dev.fallow.stardew.services.ServiceManager
import org.bukkit.plugin.java.JavaPlugin

class StardewPlugin : JavaPlugin() {
    companion object {
        lateinit var main: StardewPlugin
            private set

        val logger
            get() = main.logger
    }

    override fun onEnable() {
        main = this

        ServiceManager.register(
            StorageService.register(
                "players" to PlayerStorage,
                "farms" to FarmStorage
            ),
            PlusService,
            PlayerService,
            FarmService
        )
    }

    override fun onDisable() {
        ServiceManager.unregisterAll()
    }

}
