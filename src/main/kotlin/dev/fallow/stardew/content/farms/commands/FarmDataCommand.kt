package dev.fallow.stardew.content.farms.commands

import com.marcusslover.plus.lib.command.Command
import com.marcusslover.plus.lib.command.CommandContext
import com.marcusslover.plus.lib.command.ICommand
import com.marcusslover.plus.lib.command.TabCompleteContext
import dev.fallow.stardew.db.data.Farm
import dev.fallow.stardew.util.FeedbackType
import dev.fallow.stardew.util.SerializationHelper
import dev.fallow.stardew.util.sendFeedback
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

@Command(name = "farmdata", permission = "stardew.admin.farmdata")
object FarmDataCommand : ICommand {
    override fun execute(ctx: CommandContext): Boolean {
        val args = ctx.args
        when (args.getOrNull(0)) {
            "crop" -> when (args.getOrNull(1)) {
                "get" -> {
                    // ensure sender is player
                    val player = ctx.sender as? Player ?: run {
                        ctx.sender.sendFeedback(FeedbackType.Error, "Must be player to run this command.")
                        return true
                    }

                    // get the player's farm
                    val farm = Farm.getFarm(player) ?: run {
                        ctx.sender.sendFeedback(FeedbackType.Error, "You do not have a farm.")
                        return true
                    }

                    // get the block they're looking at
                    val lookingAt = player.getTargetBlockExact(
                        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)?.value?.toInt() ?: 6
                    )?.location ?: run {
                        player.sendFeedback(FeedbackType.Error, "Not looking at block.")
                        return true
                    }

                    // ensure they're looking at a crop
                    val cropTile = farm.getCrop(lookingAt)
                    if (cropTile == null) {
                        player.sendFeedback(FeedbackType.Error, "You are not looking at a crop.")
                    } else {
                        player.sendFeedback(
                            FeedbackType.Success,
                            "Crop data of (x,y,z)=(${lookingAt.x},${lookingAt.y},${lookingAt.z}}:"
                        )

                        val json = SerializationHelper.gson.toJson(cropTile)
                        player.sendMessage(json)
                    }
                }
                else -> ctx.sender.sendFeedback(FeedbackType.Error, "Unknown subcommand.")
            }

            else -> ctx.sender.sendFeedback(FeedbackType.Error, "Unknown subcommand.")
        }

        return true
    }

    override fun tab(ctx: TabCompleteContext): List<String> {
        val args = ctx.args
        return when (args.size) {
            1 -> tabComplete(args[0], listOf("crop"))
            2 -> when (args[0]) {
                "crop" -> tabComplete(args[1], listOf("get"))
                else -> listOf()
            }

            else -> listOf()
        }
    }
}