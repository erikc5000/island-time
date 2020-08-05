package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.ranges.toOffsetDateTimeInterval
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OffsetDateTimeIntervalSerializer : KSerializer<OffsetDateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.OffsetDateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTimeInterval {
        return decoder.decodeString().toOffsetDateTimeInterval()
    }
}