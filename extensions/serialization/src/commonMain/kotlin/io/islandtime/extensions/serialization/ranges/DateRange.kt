package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.DateRange
import io.islandtime.ranges.toDateRange
import kotlinx.serialization.*

object DateRangeSerializer : KSerializer<DateRange> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.ranges.DateRangeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateRange) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateRange {
        return decoder.decodeString().toDateRange()
    }
}