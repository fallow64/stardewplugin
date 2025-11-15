package dev.fallow.stardew.services

import com.marcusslover.plus.lib.command.CommandManager
import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.content.dev.StorageCommand
import dev.fallow.stardew.content.farms.commands.FarmCommand
import dev.fallow.stardew.content.farms.commands.FarmDataCommand

object PlusService : StardewService() {
    lateinit var commandManager: CommandManager

    override fun init() {
        commandManager = CommandManager(StardewPlugin.main).register(
            FarmCommand,
            StorageCommand,
            FarmDataCommand
        )
    }

    override fun shutdown() {
        commandManager.clearCommands()
    }
}
