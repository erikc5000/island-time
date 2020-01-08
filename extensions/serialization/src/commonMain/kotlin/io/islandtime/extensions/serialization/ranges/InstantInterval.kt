package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.toInstantInterval
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Date::class)
object InstantIntervalSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("InstantInterval")

    override fun serialize(encoder: Encoder, obj: InstantInterval) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}