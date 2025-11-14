package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.db.api.IStorageValue

/**
 * `CachedStoragValue<T>` is a type of stored value that uses an intermediate Java object to avoid
 * expensive read/writes.
 */
abstract class CachedStorageValue<T> : IStorageValue<T> {
    private var modified = false
    private var value: T? = null

    override fun load(): T {
        return value ?: rawLoad()
    }

    override fun store(value: T): T? {
        val old = this.value

        if (old != value) {
            modified = true
            this.value = value
        }

        return old
    }

    override fun flush() {
        if (modified) {
            value?.let { rawStore(it) }
        }
        modified = false
    }

    abstract fun rawLoad(): T
    abstract fun rawStore(value: T)

    override fun empty() {
        value = null
    }
}
