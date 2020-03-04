package io.islandtime.extensions.serialization

import io.islandtime.TimeZone
import io.islandtime.toTimeZone
import kotlinx.serialization.*

@Serializer(forClass = TimeZone::class)
object TimeZoneSerializer : KSerializer<TimeZone> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("TimeZone", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): TimeZone {
        return decoder.decodeString().toTimeZone()
    }
}