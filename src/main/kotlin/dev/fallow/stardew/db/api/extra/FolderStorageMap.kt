package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.db.api.IStorageMap
import dev.fallow.stardew.util.SerializationHelper
import java.io.File

/**
 * A key-value storage that maps keys to JSON files within a folder.
 */
abstract class FolderStorageMap<K, V>(
    private val folder: File,
    private val keyTransformer: (K) -> String,
    private val keyComposer: (String) -> K,
    private val valueType: Class<V>
) : IStorageMap<K, V> {
    val cache = mutableMapOf<K, V>()

    override fun init() {
        if (shouldLoadAllData) {
            loadAllData()
        }
    }

    fun loadAllData() {
        folder.listFiles().forEach { file ->
            val fileWithoutExtension = file.name.replaceFirst("[.][^.]+$", "");
            load(keyComposer(fileWithoutExtension))
        }
    }

    override fun load(key: K): V {
        cache[key]?.let { return it }

        val file = File(folder, keyTransformer(key) + ".json")

        val value = if (file.exists()) SerializationHelper.readJsonFile(file, valueType)
        else emptyValue(key)

        cache[key] = value
        return value
    }

    override fun store(key: K, value: V): V? {
        val old = cache.put(key, value)
        flush(key)
        return old
    }

    fun flush(key: K) {
        val value = cache[key] ?: return
        val file = File(folder, keyTransformer(key) + ".json")
        SerializationHelper.writeJsonFile(file, value)
    }

    override fun flushAll() {
        cache.forEach { flush(it.key) }
    }

    /**
     * The type's empty value, called when the given file is not present.
     * Returning null represents that it's impossible to create an empty value (therefore restricting silent creations).
     */
    abstract fun emptyValue(key: K): V

    /**
     * Returns whether to load all data on startup.
     */
    open val shouldLoadAllData: Boolean = false
}
