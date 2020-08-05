package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.DateRange
import io.islandtime.ranges.toDateRange
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateRangeSerializer : KSerializer<DateRange> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.ranges.DateRangeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateRange) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateRange {
        return decoder.decodeString().toDateRange()
    }
}
