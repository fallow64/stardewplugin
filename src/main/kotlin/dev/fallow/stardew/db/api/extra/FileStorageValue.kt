package dev.fallow.stardew.db.api.extra

import dev.fallow.stardew.StardewPlugin
import dev.fallow.stardew.util.SerializationHelper
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

/**
 * A storage that maps to a JSON file.
 */
abstract class FileStorageValue<T>(
    private val file: File,
    private val valueType: Class<T>
) : CachedStorageValue<T>() {
    override fun rawLoad(): T {
        if (file.exists()) {
            // attempt to read file
            try {
                FileReader(file).use { fileReader ->
                    // convert to json
                    return SerializationHelper.gson.fromJson<T>(fileReader, valueType)
                }
            } catch (e: IOException) {
                StardewPlugin.logger.severe("Could not read file: " + file.absolutePath)
                throw RuntimeException(e)
            }
        } else {
            // no file found, use default
            return emptyValue()
        }
    }

    override fun rawStore(value: T) {
        try {
            file.parentFile.mkdirs()
            FileWriter(file).use { fileWriter ->
                SerializationHelper.gson.toJson(value, fileWriter)
                fileWriter.flush()
            }
        } catch (e: IOException) {
            StardewPlugin.logger.severe("Could not write file: " + file.absolutePath)
            throw RuntimeException(e)
        }
    }

    /**
     * The type's empty value, called when the file is not present.
     * Returning null represents that it's impossible to create an empty value (therefore restricting silent creations).
     */
    abstract fun emptyValue(): T
}
