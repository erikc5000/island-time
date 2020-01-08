package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.DateTimeInterval
import io.islandtime.ranges.toDateTimeInterval
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object DateTimeIntervalSerializer : KSerializer<DateTimeInterval> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("DateTimeInterval")

    override fun serialize(encoder: Encoder, obj: DateTimeInterval) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): DateTimeInterval {
        return decoder.decodeString().toDateTimeInterval()
    }
}