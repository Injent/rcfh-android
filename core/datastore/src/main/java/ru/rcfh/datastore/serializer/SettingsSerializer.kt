package ru.rcfh.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.rcfh.datastore.model.UserSettings
import java.io.InputStream
import java.io.OutputStream

internal object SettingsSerializer : Serializer<UserSettings> {
    private val format = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }
    override val defaultValue = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        try {
            val bytes = input.readBytes()
            val string = bytes.decodeToString()
            return format.decodeFromString(string)
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read stored data", e)
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        val string = format.encodeToString(t)
        val bytes = string.encodeToByteArray()
        output.write(bytes)
    }
}