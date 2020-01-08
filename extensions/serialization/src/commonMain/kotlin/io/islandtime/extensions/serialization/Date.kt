package io.islandtime.extensions.serialization

import io.islandtime.Date
import io.islandtime.toDate
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("Date")

    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): Date {
        return decoder.decodeString().toDate()
    }
}