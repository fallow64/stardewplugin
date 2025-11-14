package dev.fallow.stardew.db.api

/**
 * `IStorage` is a simple interface for storing types of storages.
 */
sealed interface IStorage {
    /**
     * Called when the `IStorage` is initially registered.
     */
    fun init() {}

    /**
     * Called when the plugin is disabled. `StorageManager` does not automatically flush all storages on shutdown,
     * this case is covered by the default implementation.
     */
    fun shutdown() {
        flush()
    }

    /**
     * Flushes any type of caches, writing all changes permanently.
     */
    fun flush()

    /**
     * Empties all values within the storage's cache.
     */
    fun emptyCache()
}
