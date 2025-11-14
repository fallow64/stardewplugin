package dev.fallow.stardew.content.dev

import com.marcusslover.plus.lib.command.Command
import com.marcusslover.plus.lib.command.CommandContext
import com.marcusslover.plus.lib.command.ICommand
import com.marcusslover.plus.lib.command.TabCompleteContext
import dev.fallow.stardew.db.StorageService
import dev.fallow.stardew.util.FeedbackType
import dev.fallow.stardew.util.TaskHelper
import dev.fallow.stardew.util.sendFeedback

@Command(name = "storage", permission = "stardew.admin.storage")
object StorageCommand : ICommand {
    override fun execute(ctx: CommandContext): Boolean {
        val args = ctx.args
        when (args.size) {
            1 -> when (args[0]) {
                "flushall" -> {
                    TaskHelper.async {
                        StorageService.flushAll()
                        TaskHelper.sync {
                            ctx.sender.sendFeedback(FeedbackType.Success, "Successfully flushed all storage buffers.")
                        }
                    }
                }
                "emptyall" -> {
                    StorageService.emptyAllCaches()
                    ctx.sender.sendFeedback(FeedbackType.Success, "Successfully emptied all storages. This will probably cause issues.")
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
            1 -> tabComplete(args[0], listOf("flushall", "emptyall"))
            else -> listOf()
        }
    }
}