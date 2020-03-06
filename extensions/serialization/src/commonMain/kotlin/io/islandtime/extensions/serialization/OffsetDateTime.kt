package io.islandtime.extensions.serialization

import io.islandtime.OffsetDateTime
import io.islandtime.toOffsetDateTime
import kotlinx.serialization.*

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.OffsetDateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return decoder.decodeString().toOffsetDateTime()
    }
}