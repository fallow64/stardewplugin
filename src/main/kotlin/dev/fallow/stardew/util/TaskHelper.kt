package dev.fallow.stardew.util

import dev.fallow.stardew.StardewPlugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

object TaskHelper {
    val plugin
        get() = StardewPlugin.main

    fun async(runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
    }

    fun asyncTimer(delay: Long, period: Long, runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period)
    }

    fun sync(runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, runnable)
    }

    fun syncDelay(delay: Long, runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay)
    }

    fun syncTimer(delay: Long, period: Long, runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period)
    }

    fun syncRepeatUntil(delay: Long, timeout: Long, condition: () -> Boolean, then: () -> Unit) {
        var elapsed = 0L

        var task: BukkitTask? = null
        task = syncTimer(delay, 1L) {
            elapsed++

            val result = condition()
            if (result) {
                then()
                task?.cancel()
            } else if (elapsed >= timeout) {
                task?.cancel()
            }
        }
    }
}
