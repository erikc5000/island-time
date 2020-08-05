package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.toInstantInterval
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InstantIntervalSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.ranges.InstantIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InstantInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}