package io.islandtime.extensions.serialization

import io.islandtime.Time
import io.islandtime.toTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.TimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}
