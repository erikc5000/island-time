package io.islandtime.parser

import io.islandtime.base.DateProperty
import io.islandtime.format.SignStyle

/**
 * A set of predefined parsers.
 */
object DateTimeParsers {
    /**
     * ISO-8601 parsers.
     */
    object Iso {
        /**
         * Parse ISO-8601 calendar dates in either basic or extended format.
         */
        val CALENDAR_DATE = temporalParser {
            anyOf(Extended.CALENDAR_DATE, Basic.CALENDAR_DATE)
        }

        /**
         * Parse ISO-8601 ordinal dates in either basic or extended format.
         */
        val ORDINAL_DATE = temporalParser {
            anyOf(Extended.ORDINAL_DATE, Basic.ORDINAL_DATE)
        }

        /**
         * Parse ISO-8601 calendar or ordinal dates in either basic or extended format.
         */
        val DATE = temporalParser {
            anyOf(CALENDAR_DATE, ORDINAL_DATE)
        }

        /**
         * Parse an ISO-8601 time of day in either basic or extended format.
         */
        val TIME = temporalParser {
            anyOf(Extended.TIME, Basic.TIME)
        }

        /**
         * Parse an ISO-8601 UTC offset in either basic or extended format.
         */
        val UTC_OFFSET = temporalParser {
            anyOf({
                utcDesignator()
            }, {
                utcOffsetSign()
                utcOffsetHours(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    anyOf({
                        utcOffsetMinutes(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                        optional {
                            utcOffsetSeconds(2) {
                                enforceSignStyle(SignStyle.NEVER)
                            }
                        }
                    }, {
                        +':'
                        utcOffsetMinutes(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                        optional {
                            +':'
                            utcOffsetSeconds(2) {
                                enforceSignStyle(SignStyle.NEVER)
                            }
                        }
                    })
                }
            })
        }

        /**
         * Parse an ISO-8601 date and time of day in either basic or extended format.
         */
        val DATE_TIME = temporalParser {
            anyOf(Extended.DATE_TIME, Basic.DATE_TIME)
        }

        /**
         * Parse an ISO-8601 time of day and UTC offset in either basic or extended format.
         */
        val OFFSET_TIME = temporalParser {
            anyOf(Extended.OFFSET_TIME, Basic.OFFSET_TIME)
        }

        /**
         * Parse an ISO-8601 date, time of day, and UTC offset in either basic or extended format.
         *
         * Examples:
         * - `2008-09-01T18:30-4:00`
         * - `2008-09-01 18:30:00Z`
         * - `20080901 1830-04`
         */
        val OFFSET_DATE_TIME = temporalParser {
            anyOf(Extended.OFFSET_DATE_TIME, Basic.OFFSET_DATE_TIME)
        }

        /**
         * Parse an ISO-8601 date, time of day, UTC offset, and optionally, a non-standard region ID in either basic or
         * extended format.
         *
         * Examples:
         * - `2008-09-01T18:30-4:00[America/New_York]`
         * - `2008-09-01 18:30:00Z`
         * - `20080901 1830-04[America/New_York]`
         */
        val ZONED_DATE_TIME = temporalParser {
            anyOf(Extended.ZONED_DATE_TIME, Basic.ZONED_DATE_TIME)
        }

        /**
         * Parse an ISO-8601 date-time with the zero UTC offset designator in either basic or extended format.
         *
         * Examples:
         * - `2001-05-10T00:24:00.00000Z`
         * - `2001-05-10T00:24Z`
         * - `20010510 0024Z`
         */
        val INSTANT = temporalParser {
            anyOf(Extended.INSTANT, Basic.INSTANT)
        }

        /**
         * Parse an ISO-8601 year-month. The standard supports only extended format.
         *
         * Example:
         * - `2008-09`
         */
        val YEAR_MONTH = temporalParser {
            childParser(ISO_EXPANDED_YEAR_COMPONENT)
            +'-'
            childParser(ISO_MONTH_COMPONENT)
        }

        /**
         * Parse an ISO-8601 standalone year. Note that not all formats supported by this parser are valid when a year
         * is combined with other properties.
         *
         * Examples:
         * - `2008`
         * - '0001`
         * - `0000`
         * - `-0001`
         * - `+0123456789`
         * - `Y12345`
         */
        val YEAR = temporalParser {
            anyOf({
                childParser(ISO_EXPANDED_YEAR_COMPONENT)
            }, {
                // Letter-prefixed year allowed in ISO-8601-2 (for standalone year only)
                +'Y'
                year(5..9)
            })
        }

        /**
         * Parse an ISO-8601 period without any time components.
         *
         * Examples:
         * - `P5Y16M3D`
         * - `P5M-15D`
         * - `P0D`
         */
        val PERIOD = temporalParser {
            optional { periodSign() }
            +'P'
            optional {
                periodOfYears()
                +'Y'
            }
            optional {
                periodOfMonths()
                +'M'
            }
            optional {
                periodOfWeeks()
                +'W'
            }
            optional {
                periodOfDays()
                +'D'
            }
        }

        /**
         * Parse an ISO-8601 period containing only the day and time components.
         *
         * Examples:
         * - `P1DT5H6.123S`
         * - `PT15H20M`
         * - `PT0S`
         * - `-PT1S`
         */
        val DURATION = temporalParser {
            optional { periodSign() }
            +'P'
            optional {
                periodOfDays()
                +'D'
            }
            optional {
                +'T'
                optional {
                    durationOfHours()
                    +'H'
                }
                optional {
                    durationOfMinutes()
                    +'M'
                }
                optional {
                    durationOfFractionalSeconds()
                    +'S'
                }
            }
        }

        /**
         * Parse an ISO-8601 time interval between two dates in either basic or extended format.
         *
         * Examples:
         * - `1990-01-04/1991-08-30`
         * - `../19910830`
         * - `19900104/..`
         * - `../..`
         * - (empty string)
         */
        val DATE_RANGE = groupedTemporalParser {
            anyOf(Extended.DATE_RANGE, Basic.DATE_RANGE)
        }

        val DATE_TIME_INTERVAL = groupedTemporalParser {
            anyOf(Extended.DATE_TIME_INTERVAL, Basic.DATE_TIME_INTERVAL)
        }

        val OFFSET_DATE_TIME_INTERVAL = groupedTemporalParser {
            anyOf(Extended.OFFSET_DATE_TIME_INTERVAL, Basic.OFFSET_DATE_TIME_INTERVAL)
        }

        val ZONED_DATE_TIME_INTERVAL = groupedTemporalParser {
            anyOf(Extended.ZONED_DATE_TIME_INTERVAL, Basic.ZONED_DATE_TIME_INTERVAL)
        }

        val INSTANT_INTERVAL = groupedTemporalParser {
            anyOf(Extended.INSTANT_INTERVAL, Basic.INSTANT_INTERVAL)
        }

        object Basic {
            val CALENDAR_DATE = temporalParser {
                childParser(ISO_STANDARD_YEAR_COMPONENT)
                childParser(ISO_MONTH_COMPONENT)
                childParser(ISO_DAY_OF_MONTH_COMPONENT)
            }

            val ORDINAL_DATE = temporalParser {
                childParser(ISO_STANDARD_YEAR_COMPONENT)
                childParser(ISO_DAY_OF_YEAR_COMPONENT)
            }

            val DATE = temporalParser {
                childParser(ISO_STANDARD_YEAR_COMPONENT)
                anyOf({
                    childParser(ISO_MONTH_COMPONENT)
                    childParser(ISO_DAY_OF_MONTH_COMPONENT)
                }, {
                    childParser(ISO_DAY_OF_YEAR_COMPONENT)
                })
            }

            val TIME = temporalParser {
                hourOfDay(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    minuteOfHour(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        fractionalSecondOfMinute(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                }
            }

            val UTC_OFFSET = temporalParser {
                anyOf({
                    utcDesignator()
                }, {
                    utcOffsetSign()
                    utcOffsetHours(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        utcOffsetMinutes(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                        optional {
                            utcOffsetSeconds(2) {
                                enforceSignStyle(SignStyle.NEVER)
                            }
                        }
                    }
                })
            }

            /**
             * Parse an ISO-8601 date and time of day in basic format.
             *
             * Examples:
             * - `20080901T1830`
             * - `20080901 183000`
             * - `20080901 1830`
             */
            val DATE_TIME = temporalParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = temporalParser {
                childParser(TIME)
                childParser(UTC_OFFSET)
            }

            /**
             * Parse an ISO-8601 date, time of day, and UTC offset in basic format.
             *
             * Examples:
             * - `20080901T1830-400`
             * - `20080901 183000Z`
             * - `20080901 1830-04`
             */
            val OFFSET_DATE_TIME = temporalParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
            }

            /**
             * Parse an ISO-8601 date, time of day, UTC offset, and optionally, a non-standard region ID in basic
             * format.
             *
             * Examples:
             * - `20080901T1830-400[America/New_York]`
             * - `20080901 183000Z`
             * - `20080901 1830-04[America/New_York]`
             */
            val ZONED_DATE_TIME = temporalParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
                optional {
                    +'['
                    timeZoneId()
                    +']'
                }
            }

            /**
             * Parse an ISO-8601 date-time with the zero UTC offset designator in basic format.
             *
             * Examples:
             * - `20010510T002400.00000Z`
             * - `20010510T0024Z`
             * - `20010510 0024Z`
             */
            val INSTANT = temporalParser {
                childParser(DATE_TIME)
                utcDesignator()
            }

            /**
             * Parse an ISO-8601 time interval between two dates in basic format.
             *
             * Examples:
             * - `19900104/19910830`
             * - `../19910830`
             * - `19900104/..`
             * - `../..`
             * - (empty string)
             */
            val DATE_RANGE = buildIsoIntervalParser(CALENDAR_DATE)

            val DATE_TIME_INTERVAL = buildIsoIntervalParser(DATE_TIME)
            val OFFSET_DATE_TIME_INTERVAL = buildIsoIntervalParser(OFFSET_DATE_TIME)
            val ZONED_DATE_TIME_INTERVAL = buildIsoIntervalParser(ZONED_DATE_TIME)
            val INSTANT_INTERVAL = buildIsoIntervalParser(INSTANT)
        }

        object Extended {
            val CALENDAR_DATE = temporalParser {
                childParser(ISO_EXPANDED_YEAR_COMPONENT)
                +'-'
                childParser(ISO_MONTH_COMPONENT)
                +'-'
                childParser(ISO_DAY_OF_MONTH_COMPONENT)
            }

            val ORDINAL_DATE = temporalParser {
                childParser(ISO_EXPANDED_YEAR_COMPONENT)
                +'-'
                childParser(ISO_DAY_OF_YEAR_COMPONENT)
            }

            val DATE = temporalParser {
                childParser(ISO_EXPANDED_YEAR_COMPONENT)
                +'-'
                anyOf({
                    childParser(ISO_MONTH_COMPONENT)
                    +'-'
                    childParser(ISO_DAY_OF_MONTH_COMPONENT)
                }, {
                    childParser(ISO_DAY_OF_YEAR_COMPONENT)
                })
            }

            val TIME = temporalParser {
                hourOfDay(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    +':'
                    minuteOfHour(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        +':'
                        fractionalSecondOfMinute(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                }
            }

            val UTC_OFFSET = temporalParser {
                anyOf({
                    utcDesignator()
                }, {
                    utcOffsetSign()
                    utcOffsetHours(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        +':'
                        utcOffsetMinutes(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                        optional {
                            +':'
                            utcOffsetSeconds(2) {
                                enforceSignStyle(SignStyle.NEVER)
                            }
                        }
                    }
                })
            }

            /**
             * Parse an ISO-8601 date and time of day in extended format.
             *
             * Examples:
             * - `2008-09-01T18:30`
             * - `2008-09-01 18:30:00`
             * - `2008-09-01 18:30`
             */
            val DATE_TIME = temporalParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = temporalParser {
                childParser(TIME)
                childParser(UTC_OFFSET)
            }

            /**
             * Parse an ISO-8601 date, time of day, and UTC offset in extended format.
             *
             * Examples:
             * - `2008-09-01T18:30-4:00`
             * - `2008-09-01 18:30:00Z`
             * - `2008-09-01 18:30-04`
             */
            val OFFSET_DATE_TIME = temporalParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
            }

            /**
             * Parse an ISO-8601 date, time of day, UTC offset, and optionally, a non-standard region ID in extended
             * format.
             *
             * Examples:
             * - `2008-09-01T18:30-4:00[America/New_York]`
             * - `2008-09-01 18:30:00Z`
             */
            val ZONED_DATE_TIME = temporalParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
                optional {
                    +'['
                    timeZoneId()
                    +']'
                }
            }

            /**
             * Parse an ISO-8601 date-time with the zero UTC offset designator in extended format.
             *
             * Examples:
             * - `2001-05-10T00:24:00.00000Z`
             * - `2001-05-10T00:24Z`
             */
            val INSTANT = temporalParser {
                childParser(DATE_TIME)
                utcDesignator()
            }

            /**
             * Parse an ISO-8601 time interval between two dates in extended format.
             *
             * Examples:
             * - `1990-01-04/1991-08-30`
             * - `../1991-08-30`
             * - `1990-01-04/..`
             * - `../..`
             * - (empty string)
             */
            val DATE_RANGE = buildIsoIntervalParser(CALENDAR_DATE)

            val DATE_TIME_INTERVAL = buildIsoIntervalParser(DATE_TIME)
            val OFFSET_DATE_TIME_INTERVAL = buildIsoIntervalParser(OFFSET_DATE_TIME)
            val ZONED_DATE_TIME_INTERVAL = buildIsoIntervalParser(ZONED_DATE_TIME)
            val INSTANT_INTERVAL = buildIsoIntervalParser(INSTANT)
        }
    }
}

private val ISO_STANDARD_YEAR_COMPONENT = temporalParser {
    year(4) {
        enforceSignStyle(SignStyle.NEGATIVE_ONLY)
    }
}

private val ISO_EXPANDED_YEAR_COMPONENT = temporalParser {
    anyOf({
        // Expanded representation requiring sign
        year(5..10) { enforceSignStyle(SignStyle.ALWAYS) }
    }, {
        // Standard 4-digit year
        year(4)
    })
}

private val ISO_MONTH_COMPONENT = temporalParser {
    monthNumber(2) {
        enforceSignStyle(SignStyle.NEVER)
    }
}

private val ISO_DAY_OF_MONTH_COMPONENT = temporalParser {
    dayOfMonth(2) {
        enforceSignStyle(SignStyle.NEVER)
    }
}

private val ISO_DAY_OF_YEAR_COMPONENT = temporalParser {
    dayOfYear(3) {
        enforceSignStyle(SignStyle.NEVER)
    }
}

private fun buildIsoIntervalParser(elementParser: TemporalParser): GroupedTemporalParser {
    return groupedTemporalParser {
        anyOf({
            group {
                anyOf({ unboundedDesignator(DateProperty.IsFarPast) }, { childParser(elementParser) })
            }
            +'/'
            group {
                anyOf({ unboundedDesignator(DateProperty.IsFarFuture) }, { childParser(elementParser) })
            }
        }, {
            repeat(2) { group {} }
        })
    }
}