package io.islandtime.extensions.serialization

import io.islandtime.ZonedDateTime
import io.islandtime.toZonedDateTime
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = ZonedDateTime::class)
object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("ZonedDateTime")

    override fun serialize(encoder: Encoder, obj: ZonedDateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return decoder.decodeString().toZonedDateTime()
    }
}