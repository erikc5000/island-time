@file:Suppress("FunctionName")

package io.islandtime.formatter

import io.islandtime.formatter.dsl.IsoDateTimeFormatterBuilder
import io.islandtime.formatter.internal.IsoDateTimeFormatterBuilderImpl

inline fun IsoDateTimeFormatter(builder: IsoDateTimeFormatterBuilder.() -> Unit = {}): TemporalFormatter {
    return IsoDateTimeFormatterBuilderImpl().apply(builder).build()
}
