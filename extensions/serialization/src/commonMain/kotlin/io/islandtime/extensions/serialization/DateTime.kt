package io.islandtime.extensions.serialization

import io.islandtime.DateTime
import io.islandtime.toDateTime
import kotlinx.serialization.*

@Serializer(forClass = DateTime::class)
object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("DateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTime {
        return decoder.decodeString().toDateTime()
    }
}