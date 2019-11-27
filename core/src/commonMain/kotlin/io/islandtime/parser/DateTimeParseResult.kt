package io.islandtime.parser

import io.islandtime.base.DateTimeField

/**
 * The result of a parsing operation.
 */
data class DateTimeParseResult(
    val fields: MutableMap<DateTimeField, Long> = hashMapOf(),
    var timeZoneId: String? = null
) {
    fun isEmpty() = fields.isEmpty() && timeZoneId == null
    fun isNotEmpty() = !isEmpty()

    internal fun deepCopy() = DateTimeParseResult(timeZoneId = timeZoneId).apply {
        fields.putAll(this@DateTimeParseResult.fields)
    }
}