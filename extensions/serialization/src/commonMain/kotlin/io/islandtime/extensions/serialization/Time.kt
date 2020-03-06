package io.islandtime.extensions.serialization

import io.islandtime.Time
import io.islandtime.toTime
import kotlinx.serialization.*

object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.TimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}