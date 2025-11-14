package dev.fallow.stardew.content.farms.commands

import com.marcusslover.plus.lib.command.Command
import com.marcusslover.plus.lib.command.CommandContext
import com.marcusslover.plus.lib.command.ICommand
import com.marcusslover.plus.lib.command.TabCompleteContext
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.db.storages.FarmStorage
import dev.fallow.stardew.db.storages.PlayerStorage
import dev.fallow.stardew.util.FeedbackType
import dev.fallow.stardew.util.sendFeedback
import org.bukkit.entity.Player
import java.util.UUID

@Command(name = "farm")
object FarmCommand : ICommand {
    override fun execute(ctx: CommandContext): Boolean {
        val player = ctx.sender as? Player ?: run {
            ctx.sender.sendFeedback(FeedbackType.Error, "Must be player to use this command.")
            return true
        }

        val args = ctx.args
        when (args.getOrNull(0)) {
            "create" -> {
                val playerData = PlayerStorage.load(player)
                if (playerData.farmId != null) {
                    player.sendFeedback(FeedbackType.Error, "You cannot join more than one farm.")
                    return true
                }

                val newFarmId = UUID.randomUUID()
                val newFarm = Farm(
                    uniqueId = newFarmId,
                    players = mutableListOf(player.uniqueId),
                    cropTiles = mutableMapOf()
                )

                FarmStorage.store(newFarmId, newFarm)
                PlayerStorage.write(player.uniqueId) {
                    it.farmId = newFarmId
                }

                player.sendFeedback(FeedbackType.Success, "Created new farm!")
                return true
            }
            "status" -> {
                val playerData = PlayerStorage.load(player)

                val farmId = playerData.farmId
                if (farmId != null) {
                    player.sendFeedback(FeedbackType.Info, "You are currently in the farm with ID ${playerData.farmId}.")
                } else {
                    player.sendFeedback(FeedbackType.Info, "You are not in a farm.")
                }

                return true
            }
            else -> {
                player.sendFeedback(FeedbackType.Error, "Unknown subcommand.")
                return true
            }
        }
    }

    override fun tab(ctx: TabCompleteContext): List<String> {
        val args = ctx.args
        return when (args.size) {
            1 -> tabComplete(args[0], listOf("create", "status"))
            else -> listOf()
        }
    }
}