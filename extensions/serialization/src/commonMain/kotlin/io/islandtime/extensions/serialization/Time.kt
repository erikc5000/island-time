package io.islandtime.extensions.serialization

import io.islandtime.Time
import io.islandtime.toTime
import kotlinx.serialization.*

@Serializer(forClass = Time::class)
object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Time", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}