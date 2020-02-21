package io.islandtime.parser

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
        val CALENDAR_DATE = dateTimeParser {
            anyOf(Basic.CALENDAR_DATE, Extended.CALENDAR_DATE)
        }

        /**
         * Parse ISO-8601 ordinal dates in either basic or extended format.
         */
        val ORDINAL_DATE = dateTimeParser {
            anyOf(Basic.ORDINAL_DATE, Extended.ORDINAL_DATE)
        }

        /**
         * Parse ISO-8601 calendar or ordinal dates in either basic or extended format.
         */
        val DATE = dateTimeParser {
            anyOf(CALENDAR_DATE, ORDINAL_DATE)
        }

        /**
         * Parse an ISO-8601 time of day in either basic or extended format.
         */
        val TIME = dateTimeParser {
            anyOf(Basic.TIME, Extended.TIME)
        }

        /**
         * Parse an ISO-8601 time shift in either basic or extended format.
         */
        val UTC_OFFSET = dateTimeParser {
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
        val DATE_TIME = dateTimeParser {
            anyOf(Basic.DATE_TIME, Extended.DATE_TIME)
        }

        /**
         * Parse an ISO-8601 time of day and UTC offset in either basic or extended format.
         */
        val OFFSET_TIME = dateTimeParser {
            anyOf(Basic.OFFSET_TIME, Extended.OFFSET_TIME)
        }

        /**
         * Parse an ISO-8601 date, time of day, and UTC offset in either basic or extended format.
         *
         * Examples:
         * - `2008-09-01T18:30-4:00`
         * - `2008-09-01 18:30:00Z`
         * - `20080901 1830-04`
         */
        val OFFSET_DATE_TIME = dateTimeParser {
            anyOf(Basic.OFFSET_DATE_TIME, Extended.OFFSET_DATE_TIME)
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
        val ZONED_DATE_TIME = dateTimeParser {
            anyOf(Basic.ZONED_DATE_TIME, Extended.ZONED_DATE_TIME)
        }

        /**
         * Parse an ISO-8601 date-time with the zero UTC offset designator in either basic or extended format.
         *
         * Examples:
         * - `2001-05-10T00:24:00.00000Z`
         * - `2001-05-10T00:24Z`
         * - `20010510 0024Z`
         */
        val INSTANT = dateTimeParser {
            anyOf(Basic.INSTANT, Extended.INSTANT)
        }

        /**
         * Parse an ISO-8601 year-month. The standard supports only extended format.
         *
         * Example:
         * - `2008-09`
         */
        val YEAR_MONTH = dateTimeParser {
            anyOf({
                // Expanded representation requiring sign
                year(5..10) { enforceSignStyle(SignStyle.ALWAYS) }
            }, {
                // Standard 4-digit year
                year(4)
            })
            +'-'
            monthNumber(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        /**
         * Parse an ISO-8601 standalone year.
         *
         * Examples:
         * - `2008`
         * - '0001`
         * - `0000`
         * - `-0001`
         * - `+0123456789`
         * - `Y12345`
         */
        val YEAR = dateTimeParser {
            anyOf({
                // Expanded representation requiring sign
                year(5..10) { enforceSignStyle(SignStyle.ALWAYS) }
            }, {
                // Standard 4-digit year
                year(4)
            }, {
                // Letter-prefixed year allowed in ISO-8601-2 (for standalone year only)
                +'Y'
                year(5..10)
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
        val PERIOD = dateTimeParser {
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
        val DURATION = dateTimeParser {
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
        val DATE_RANGE = groupedDateTimeParser {
            anyOf(Basic.DATE_RANGE, Extended.DATE_RANGE)
        }

        val DATE_TIME_INTERVAL = groupedDateTimeParser {
            anyOf(Basic.DATE_TIME_INTERVAL, Extended.DATE_TIME_INTERVAL)
        }

        val OFFSET_DATE_TIME_INTERVAL = groupedDateTimeParser {
            anyOf(Basic.OFFSET_DATE_TIME_INTERVAL, Extended.OFFSET_DATE_TIME_INTERVAL)
        }

        val ZONED_DATE_TIME_INTERVAL = groupedDateTimeParser {
            anyOf(Basic.ZONED_DATE_TIME_INTERVAL, Extended.ZONED_DATE_TIME_INTERVAL)
        }

        val INSTANT_INTERVAL = groupedDateTimeParser {
            anyOf(Basic.INSTANT_INTERVAL, Extended.INSTANT_INTERVAL)
        }

        object Basic {
            val CALENDAR_DATE = dateTimeParser {
                year(4) {
                    enforceSignStyle(SignStyle.NEGATIVE_ONLY)
                }
                monthNumber(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                dayOfMonth(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
            }

            val ORDINAL_DATE = dateTimeParser {
                year(4) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                dayOfYear(3) {
                    enforceSignStyle(SignStyle.NEVER)
                }
            }

            val DATE = dateTimeParser {
                anyOf(CALENDAR_DATE, ORDINAL_DATE)
            }

            val TIME = dateTimeParser {
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

            val UTC_OFFSET = dateTimeParser {
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
            val DATE_TIME = dateTimeParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = dateTimeParser {
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
            val OFFSET_DATE_TIME = dateTimeParser {
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
            val ZONED_DATE_TIME = dateTimeParser {
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
            val INSTANT = dateTimeParser {
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
            val CALENDAR_DATE = dateTimeParser {
                year(4..10)
                +'-'
                monthNumber(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                +'-'
                dayOfMonth(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
            }

            val ORDINAL_DATE = dateTimeParser {
                year(4) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                +'-'
                dayOfYear(3) {
                    enforceSignStyle(SignStyle.NEVER)
                }
            }

            val DATE = dateTimeParser {
                anyOf(CALENDAR_DATE, ORDINAL_DATE)
            }

            val TIME = dateTimeParser {
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

            val UTC_OFFSET = dateTimeParser {
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
            val DATE_TIME = dateTimeParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = dateTimeParser {
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
            val OFFSET_DATE_TIME = dateTimeParser {
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
            val ZONED_DATE_TIME = dateTimeParser {
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
            val INSTANT = dateTimeParser {
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

private fun buildIsoIntervalParser(elementParser: DateTimeParser): GroupedDateTimeParser {
    return groupedDateTimeParser {
        anyOf({
            group {
                anyOf({ unboundedDesignator() }, { childParser(elementParser) })
            }
            +'/'
            group {
                anyOf({ unboundedDesignator() }, { childParser(elementParser) })
            }
        }, {
            repeat(2) { group {} }
        })
    }
}