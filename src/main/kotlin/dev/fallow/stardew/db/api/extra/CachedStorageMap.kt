package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.db.api.IStorageMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * `CachedStorageMap<K, V>` is a type of storage map that uses an intermediate in-memory map to prevent
 * example read/writes.
 */
abstract class CachedStorageMap<K, V> : IStorageMap<K, V> {
    protected val cache: ConcurrentMap<K, V> = ConcurrentHashMap()
    private val dirtyKeys = ConcurrentHashMap.newKeySet<K>()

    override fun load(key: K): V {
        return cache.computeIfAbsent(key) { rawLoad(key) }
    }

    override fun store(key: K, value: V): V? {
        var oldValue: V? = null

        cache.compute(key) { _, ov ->
            oldValue = ov

            // if the stored value isn't the same, then mark it as dirty (modified)
            if (oldValue != value) {
                dirtyKeys.add(key)
            }

            value
        }

        // then return the old value
        return oldValue
    }

    override fun flush() {
        if (dirtyKeys.isEmpty()) return

        // collect all entries to flush
        val entriesToFlush = mutableMapOf<K, V>()
        for (key in dirtyKeys) {
            val value = cache[key]
            if (value != null) {
                entriesToFlush[key] = value
            }
        }

        if (entriesToFlush.isNotEmpty()) {
            try {
                rawStoreBatch(entriesToFlush)

                // remove dirty keys only if cache hasn't been updated
                for ((key, value) in entriesToFlush.entries) {
                    val cachedValue = cache[key]
                    if (cachedValue == value) {
                        dirtyKeys.remove(key)
                    }
                }
            } catch (ex: Exception) {
                // failed to store, keep cache dirty
                StardewPlugin.logger.warning("Failed to batch store ${entriesToFlush.size} entries")
                ex.printStackTrace()
            }
        }
    }

    override fun empty() {
        cache.clear()
        dirtyKeys.clear()
    }

    abstract fun rawLoad(key: K): V
    abstract fun rawStore(key: K, value: V)
    abstract fun rawStoreBatch(entries: Map<K, V>)
}
