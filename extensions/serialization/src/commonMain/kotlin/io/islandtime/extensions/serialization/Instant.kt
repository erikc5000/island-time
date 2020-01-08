package io.islandtime.extensions.serialization

import io.islandtime.Instant
import io.islandtime.toInstant
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("Instant")

    override fun serialize(encoder: Encoder, obj: Instant) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return decoder.decodeString().toInstant()
    }
}