package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.ranges.toOffsetDateTimeInterval
import kotlinx.serialization.*

object OffsetDateTimeIntervalSerializer : KSerializer<OffsetDateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.OffsetDateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTimeInterval {
        return decoder.decodeString().toOffsetDateTimeInterval()
    }
}