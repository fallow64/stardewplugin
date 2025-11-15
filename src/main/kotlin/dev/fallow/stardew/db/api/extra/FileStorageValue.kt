package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.db.api.IStorageValue
import dev.fallow.stardew.util.SerializationHelper
import java.io.File

/**
 * A storage that maps to a JSON file.
 */
abstract class FileStorageValue<T>(
    private val file: File, private val valueType: Class<T>
) : IStorageValue<T> {
    var cache: T? = null

    override fun load(): T {
        cache?.let { return it }

        val result = if (file.exists()) SerializationHelper.readJsonFile(file, valueType)
        else emptyValue()

        cache = result
        return result
    }

    override fun store(value: T): T? {
        val old = cache
        cache = value
        SerializationHelper.writeJsonFile(file, value)
        return old
    }

    fun flush() {
        cache?.let { cached ->
            SerializationHelper.writeJsonFile(file, cached)
        }
    }

    override fun flushAll() {
        flush()
    }

    /**
     * The type's empty value, called when the file is not present.
     * Returning null represents that it's impossible to create an empty value (therefore restricting silent creations).
     */
    abstract fun emptyValue(): T
}
