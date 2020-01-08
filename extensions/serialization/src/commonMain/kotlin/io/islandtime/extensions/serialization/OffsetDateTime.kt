package io.islandtime.extensions.serialization

import io.islandtime.OffsetDateTime
import io.islandtime.toOffsetDateTime
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = OffsetDateTime::class)
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("OffsetDateTime")

    override fun serialize(encoder: Encoder, obj: OffsetDateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return decoder.decodeString().toOffsetDateTime()
    }
}