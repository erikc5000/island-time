package io.islandtime.locale

import moment.localeData
import moment.moment

class MLocale(
    locale: moment.Locale? = null
) : moment.Locale by locale ?: moment().localeData()

actual typealias Locale = MLocale

actual fun defaultLocale(): Locale =
    MLocale()

internal actual fun localeOf(identifier: String): Locale =
    MLocale(localeData(identifier))