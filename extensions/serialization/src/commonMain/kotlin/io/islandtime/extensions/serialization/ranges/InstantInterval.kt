package io.islandtime.extensions.serialization.ranges

import io.islandtime.Date
import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.toInstantInterval
import kotlinx.serialization.*

@Serializer(forClass = Date::class)
object InstantIntervalSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("InstantInterval", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InstantInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}