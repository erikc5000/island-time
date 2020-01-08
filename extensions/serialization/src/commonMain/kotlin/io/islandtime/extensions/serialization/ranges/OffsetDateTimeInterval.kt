package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.ranges.toOffsetDateTimeInterval
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object OffsetDateTimeIntervalSerializer : KSerializer<OffsetDateTimeInterval> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("OffsetDateTimeInterval")

    override fun serialize(encoder: Encoder, obj: OffsetDateTimeInterval) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTimeInterval {
        return decoder.decodeString().toOffsetDateTimeInterval()
    }
}