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

    private val _storages = mutableMapOf<String, IStorage>()
    private var flushTask: BukkitTask? = null

    val storages
        get() = _storages.toMap()

    override fun init() {
        // note: storage initialization happens before
        // automatically flush storages every 30 seconds
        flushTask = TaskHelper.asyncTimer(STORAGE_FLUSH_PERIOD, STORAGE_FLUSH_PERIOD, this::flushAll)
    }

    override fun shutdown() {
        flushTask?.cancel()
        _storages.values.forEach { it.shutdown() }
    }

    /** Register a given storage under an id. */
    fun register(vararg storages: Pair<String, IStorage>) = apply {
        storages.forEach { (id, storage) ->
            require(!this._storages.containsKey(id)) { "duplicate storage id $id registered twice" }
            this._storages[id] = storage
            storage.init()
        }
    }

    /** Flush data from the storage, essentially backing it up. */
    fun flushAll() {
        plugin.logger.info("Flushing storage buffers")
        val timeTaken = measureTime {
            _storages.values.forEach { it.flushAll() }
        }
        plugin.logger.info("Flush storage buffers took: $timeTaken")
    }
}