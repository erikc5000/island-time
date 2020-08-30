package io.islandtime.properties

import io.islandtime.TimeZone
import io.islandtime.base.BooleanProperty
import io.islandtime.base.ObjectProperty
import io.islandtime.base.StringProperty

/**
 * A property of a time zone.
 */
sealed class TimeZoneProperty {
    /**
     * The [TimeZone] object.
     */
    object TimeZoneObject : TimeZoneProperty(), ObjectProperty<TimeZone>

    /**
     * The ID associated with the time zone. This is generally an IANA time zone database identifier or fixed UTC
     * offset.
     */
    object Id : TimeZoneProperty(), StringProperty

    /**
     * Is this a fixed offset time zone?
     */
    object IsFixedOffset : TimeZoneProperty(), BooleanProperty
}
