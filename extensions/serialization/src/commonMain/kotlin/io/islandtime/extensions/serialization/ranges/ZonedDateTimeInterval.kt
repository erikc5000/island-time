package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.ranges.toZonedDateTimeInterval
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object ZonedDateTimeIntervalSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("ZonedDateTimeInterval")

    override fun serialize(encoder: Encoder, obj: ZonedDateTimeInterval) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}