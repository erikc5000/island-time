package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale
import io.islandtime.measures.days

internal actual fun systemDefaultWeekSettings(): WeekSettings {
    //TODO we have to figure out better ways to find the `minimumDaysInFirstWeek
    return WeekSettings(
        firstIslandDayOfWeek,
        7
    )
}

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = firstIslandDayOfWeek(this)

internal fun firstIslandDayOfWeek(locale: Locale = defaultLocale()): DayOfWeek =
    //according to https://github.com/moment/luxon/issues/373 there is no way to get this info from Intl API
    //but based on https://gist.github.com/wkeese/20514beb68bc1bac807ec07cda4175db we could copy that
    DayOfWeek.SUNDAY + firstDayOfWeek(locale.locale).days


internal val firstIslandDayOfWeek: DayOfWeek
    get() = firstIslandDayOfWeek()


val firstDay = mapOf(/*default is 1=Monday*/
    "bd" to 5,
    "mv" to 5,
    "ae" to 6,
    "af" to 6,
    "bh" to 6,
    "dj" to 6,
    "dz" to 6,
    "eg" to 6,
    "iq" to 6,
    "ir" to 6,
    "jo" to 6,
    "kw" to 6,
    "ly" to 6,
    "ma" to 6,
    "om" to 6,
    "qa" to 6,
    "sa" to 6,
    "sd" to 6,
    "sy" to 6,
    "ye" to 6,
    "ag" to 0,
    "ar" to 0,
    "as" to 0,
    "au" to 0,
    "br" to 0,
    "bs" to 0,
    "bt" to 0,
    "bw" to 0,
    "by" to 0,
    "bz" to 0,
    "ca" to 0,
    "cn" to 0,
    "co" to 0,
    "dm" to 0,
    "do" to 0,
    "et" to 0,
    "gt" to 0,
    "gu" to 0,
    "hk" to 0,
    "hn" to 0,
    "id" to 0,
    "ie" to 0,
    "il" to 0,
    "in" to 0,
    "jm" to 0,
    "jp" to 0,
    "ke" to 0,
    "kh" to 0,
    "kr" to 0,
    "la" to 0,
    "mh" to 0,
    "mm" to 0,
    "mo" to 0,
    "mt" to 0,
    "mx" to 0,
    "mz" to 0,
    "ni" to 0,
    "np" to 0,
    "nz" to 0,
    "pa" to 0,
    "pe" to 0,
    "ph" to 0,
    "pk" to 0,
    "pr" to 0,
    "py" to 0,
    "sg" to 0,
    "sv" to 0,
    "th" to 0,
    "tn" to 0,
    "tt" to 0,
    "tw" to 0,
    "um" to 0,
    "us" to 0,
    "ve" to 0,
    "vi" to 0,
    "ws" to 0,
    "za" to 0,
    "zw" to 0
)

fun firstDayOfWeek (locale : String) : Int {
    val country = locale.split("-")[1].toLowerCase()
    return firstDay.getOrElse(country) { 1 }
}