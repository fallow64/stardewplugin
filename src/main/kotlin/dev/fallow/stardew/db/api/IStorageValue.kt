package dev.fallow.stardew.db.api

/**
 * `IStorageValue<T>` stores a single instance of a value.
 */
interface IStorageValue<T> : IStorage {
    /**
     * Load the value
     */
    fun load(): T

    /**
     * Store the value. Returns the old value (if any)
     */
    fun store(value: T): T?

    /**
     * Modify the stored value.
     */
    fun write(map: (T) -> Unit) {
        val value = load()
        map(value)
        store(value)
    }
}