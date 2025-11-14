package dev.fallow.stardew.util

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

object SerializationHelper {
    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .registerTypeAdapter(Location3i::class.java, Location3iTypeAdapter)
        .create()

    object Location3iTypeAdapter : TypeAdapter<Location3i>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Location3i?) {
            if (value == null) {
                out.nullValue()
                return
            }
            out.value(value.toString())
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): Location3i? {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }

            val raw = reader.nextString()
            // Expected basic format: Location3i(uuid,x,y,z)
            if (!raw.startsWith("Location3i(") || !raw.endsWith(")")) {
                throw JsonParseException("Invalid Location3i format: '$raw'")
            }

            val inside = raw.substringAfter("Location3i(").substringBeforeLast(")")
            val parts = inside.split(",")

            if (parts.size != 4) {
                throw JsonParseException("Location3i requires 4 fields, got ${parts.size}: '$raw'")
            }

            try {
                val farmId = UUID.fromString(parts[0])
                val x = parts[1].toInt()
                val y = parts[2].toInt()
                val z = parts[3].toInt()

                return Location3i(farmId, x, y, z)

            } catch (e: Exception) {
                throw JsonParseException("Failed to parse Location3i: '$raw'", e)
            }
        }
    }
}