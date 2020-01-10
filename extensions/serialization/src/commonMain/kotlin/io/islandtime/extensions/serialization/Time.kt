package io.islandtime.extensions.serialization

import io.islandtime.Time
import io.islandtime.toTime
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Time::class)
object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("Time")

    override fun serialize(encoder: Encoder, obj: Time) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}