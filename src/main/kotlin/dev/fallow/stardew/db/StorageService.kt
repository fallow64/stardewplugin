package dev.fallow.stardew.db

import dev.fallow.stardew.db.api.IStorage
import dev.fallow.stardew.services.StardewService
import dev.fallow.stardew.util.TaskHelper
import org.bukkit.scheduler.BukkitTask
import kotlin.time.measureTime

/**
 * `StorageManager` manages different types of storages, initializes needed data, etc.
 */
object StorageService : StardewService() {
    private const val STORAGE_FLUSH_PERIOD = 60 * 20L

    private var flushTask: BukkitTask? = null
    private val storages = mutableListOf<IStorage>()

    fun register(vararg storages: IStorage) = apply {
        storages.forEach {
            this.storages.add(it)
            it.init()
        }
    }

    override fun init() {
        // note: storage initialization happens before
        // automatically flush storages every 30 seconds
        flushTask = TaskHelper.asyncTimer(STORAGE_FLUSH_PERIOD, STORAGE_FLUSH_PERIOD, this::flush)
    }

    override fun shutdown() {
        flushTask?.cancel()

        storages.forEach { it.shutdown() }
    }

    fun flush() {
        plugin.logger.info("Flushing storage buffers")
        val timeTaken = measureTime {
            storages.forEach { it.flush() }
        }
        plugin.logger.info("Flush storage buffers took: $timeTaken")
    }

    fun emptyAll() {
        storages.forEach {
            it.empty()
        }
    }
}