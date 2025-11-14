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
                "list" -> {
                    val storageKeys = StorageService.storages.keys.sorted()
                    if (storageKeys.isEmpty()) {
                        ctx.sender.sendFeedback(FeedbackType.Info, "No storages registered.")
                    } else {
                        ctx.sender.sendFeedback(FeedbackType.Info, "Registered storages:")
                        storageKeys.forEach { key ->
                            ctx.sender.sendMessage("  - $key")
                        }
                    }
                }
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
                else -> ctx.sender.sendFeedback(FeedbackType.Error, "Unknown subcommand. Use: list, flush <storage>, flushall, empty <storage>, emptyall")
            }
            2 -> {
                val storageKey = args[1]
                val storage = StorageService.storages[storageKey]
                if (storage == null) {
                    ctx.sender.sendFeedback(FeedbackType.Error, "Storage '$storageKey' not found. Use /storage list to see available storages.")
                    return true
                }

                when (args[0]) {
                    "flush" -> {
                        TaskHelper.async {
                            storage.flush()
                            TaskHelper.sync {
                                ctx.sender.sendFeedback(FeedbackType.Success, "Successfully flushed storage '$storageKey'.")
                            }
                        }
                    }
                    "empty" -> {
                        storage.emptyCache()
                        ctx.sender.sendFeedback(FeedbackType.Success, "Successfully emptied cache for storage '$storageKey'.")
                    }
                    else -> ctx.sender.sendFeedback(FeedbackType.Error, "Unknown subcommand. Use: list, flush <storage>, flushall, empty <storage>, emptyall")
                }
            }
            else -> ctx.sender.sendFeedback(FeedbackType.Error, "Usage: /storage <list|flush|flushall|empty|emptyall> [storage]")
        }

        return true
    }

    override fun tab(ctx: TabCompleteContext): List<String> {
        val args = ctx.args
        return when (args.size) {
            1 -> tabComplete(args[0], listOf("list", "flush", "flushall", "empty", "emptyall"))
            2 -> {
                when (args[0]) {
                    "flush", "empty" -> tabComplete(args[1], StorageService.storages.keys.toList())
                    else -> listOf()
                }
            }
            else -> listOf()
        }
    }
}