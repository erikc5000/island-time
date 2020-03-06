package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.ranges.toZonedDateTimeInterval
import kotlinx.serialization.*

object ZonedDateTimeIntervalSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.ZonedDateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}