package io.islandtime.extensions.serialization

import io.islandtime.TimeZone
import io.islandtime.toTimeZone
import kotlinx.serialization.*

object TimeZoneSerializer : KSerializer<TimeZone> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.TimeZoneSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): TimeZone {
        return decoder.decodeString().toTimeZone()
    }
}