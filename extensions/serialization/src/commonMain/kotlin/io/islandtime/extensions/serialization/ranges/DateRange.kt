package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.DateRange
import io.islandtime.ranges.toDateRange
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object DateRangeSerializer : KSerializer<DateRange> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("DateRange")

    override fun serialize(encoder: Encoder, obj: DateRange) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): DateRange {
        return decoder.decodeString().toDateRange()
    }
}