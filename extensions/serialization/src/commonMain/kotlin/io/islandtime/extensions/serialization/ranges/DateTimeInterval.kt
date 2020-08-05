package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.DateTimeInterval
import io.islandtime.ranges.toDateTimeInterval
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateTimeIntervalSerializer : KSerializer<DateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.ranges.DateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTimeInterval {
        return decoder.decodeString().toDateTimeInterval()
    }
}