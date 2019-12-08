package io.islandtime.format

import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    override fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}