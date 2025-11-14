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
        .registerTypeAdapter(FarmLocation3i::class.java, FarmLocation3iTypeAdapter)
        .create()

    object FarmLocation3iTypeAdapter : TypeAdapter<FarmLocation3i>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: FarmLocation3i?) {
            if (value == null) {
                out.nullValue()
                return
            }
            out.value(value.toString())
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): FarmLocation3i? {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }

            val raw = reader.nextString()
            // Expected basic format: FarmLocation3i(uuid,x,y,z)
            if (!raw.startsWith("FarmLocation3i(") || !raw.endsWith(")")) {
                throw JsonParseException("Invalid Location3i format: '$raw'")
            }

            val inside = raw.substringAfter("FarmLocation3i(").substringBeforeLast(")")
            val parts = inside.split(",")

            if (parts.size != 4) {
                throw JsonParseException("FarmLocation3i requires 4 fields, got ${parts.size}: '$raw'")
            }

            try {
                val farmId = UUID.fromString(parts[0])
                val x = parts[1].toInt()
                val y = parts[2].toInt()
                val z = parts[3].toInt()

                return FarmLocation3i(farmId, x, y, z)

            } catch (e: Exception) {
                throw JsonParseException("Failed to parse FarmLocation3i: '$raw'", e)
            }
        }
    }
}