package io.islandtime.extensions.serialization

import io.islandtime.YearMonth
import io.islandtime.toYearMonth
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = YearMonth::class)
object YearMonthSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("YearMonth")

    override fun serialize(encoder: Encoder, obj: YearMonth) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return decoder.decodeString().toYearMonth()
    }
}