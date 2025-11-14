package dev.fallow.stardew.db.api

/**
 * `IStorageMap<K, V>` is a type of storage that maps keys to objects.
 */
interface IStorageMap<K, V> : IStorage {
    /**
     * Load a given key
     */
    fun load(key: K): V

    /**
     * Store a given key with its value
     */
    fun store(key: K, value: V): V?

    /**
     * Modify the value of the given key. Returns the new value
     */
    fun write(key: K, map: (V) -> Unit) {
        val value = load(key)
        map(value)
        store(key, value)
    }
}
