package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.ranges.toZonedDateTimeInterval
import kotlinx.serialization.*

@Serializer(forClass = Date::class)
object ZonedDateTimeIntervalSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ZonedDateTimeInterval", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}