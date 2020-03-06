package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.DateTimeInterval
import io.islandtime.ranges.toDateTimeInterval
import kotlinx.serialization.*

object DateTimeIntervalSerializer : KSerializer<DateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.ranges.DateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTimeInterval {
        return decoder.decodeString().toDateTimeInterval()
    }
}