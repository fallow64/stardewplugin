package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.util.SerializationHelper
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

/**
 * A key-value storage that maps keys to JSON files within a folder.
 */
abstract class FolderStorageMap<K, V>(
    private val folder: File,
    private val keyTransformer: (K) -> String,
    private val keyComposer: (String) -> K,
    private val valueType: Class<V>
) : CachedStorageMap<K, V>() {
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

    override fun rawLoad(key: K): V {
        val fileName = keyTransformer(key) + ".json"
        val file = File(folder, fileName)

        if (file.exists()) {
            // attempt to read file
            try {
                FileReader(file).use { fileReader ->
                    // convert to json
                    return SerializationHelper.gson.fromJson<V>(fileReader, valueType)
                }
            } catch (e: IOException) {
                StardewPlugin.logger.severe("Could not read file: " + file.absolutePath)
                throw RuntimeException(e)
            }
        } else {
            // no file found, use default
            return emptyValue(key)
        }
    }

    override fun rawStore(key: K, value: V) {
        val fileName = keyTransformer(key) + ".json"
        val file = File(folder, fileName)

        try {
            file.parentFile.mkdirs() // ensure the folder exists BEFORE opening the writer
            FileWriter(file).use { writer ->
                SerializationHelper.gson.toJson(value, writer)
            }
        } catch (e: IOException) {
            StardewPlugin.logger.severe("Could not write file: ${file.absolutePath}")
            throw RuntimeException(e)
        }
    }

    override fun rawStoreBatch(entries: Map<K, V>) {
        for ((key, value) in entries) {
            rawStore(key, value)
        }
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
