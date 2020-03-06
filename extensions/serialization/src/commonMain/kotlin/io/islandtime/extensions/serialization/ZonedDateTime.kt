package io.islandtime.extensions.serialization

import io.islandtime.ZonedDateTime
import io.islandtime.toZonedDateTime
import kotlinx.serialization.*

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.ZonedDateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return decoder.decodeString().toZonedDateTime()
    }
}