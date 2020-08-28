package io.islandtime.extensions.serialization

import io.islandtime.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimeZoneSerializer : KSerializer<TimeZone> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.TimeZoneSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): TimeZone {
        return TimeZone(decoder.decodeString())
    }
}
