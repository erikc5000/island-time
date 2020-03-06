package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.toInstantInterval
import kotlinx.serialization.*

object InstantIntervalSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.ranges.InstantIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InstantInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}