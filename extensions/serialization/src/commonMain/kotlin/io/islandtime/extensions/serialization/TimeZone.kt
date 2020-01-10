package io.islandtime.extensions.serialization

import io.islandtime.TimeZone
import io.islandtime.toTimeZone
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = TimeZone::class)
object TimeZoneSerializer : KSerializer<TimeZone> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("TimeZone")

    override fun serialize(encoder: Encoder, obj: TimeZone) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): TimeZone {
        return decoder.decodeString().toTimeZone()
    }
}