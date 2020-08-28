package io.islandtime.extensions.serialization.ranges

import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.ranges.toZonedDateTimeInterval
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ZonedDateTimeIntervalSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.ZonedDateTimeIntervalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}
