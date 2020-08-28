package io.islandtime.extensions.serialization

import io.islandtime.OffsetDateTime
import io.islandtime.toOffsetDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.OffsetDateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return decoder.decodeString().toOffsetDateTime()
    }
}
