package io.islandtime.extensions.serialization

import io.islandtime.DateTime
import io.islandtime.toDateTime
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = DateTime::class)
object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("DateTime")

    override fun serialize(encoder: Encoder, obj: DateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): DateTime {
        return decoder.decodeString().toDateTime()
    }
}