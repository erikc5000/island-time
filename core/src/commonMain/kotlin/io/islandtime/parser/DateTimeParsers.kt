package io.islandtime.parser

/**
 * A set of predefined parsers
 */
object DateTimeParsers {
    /**
     * ISO-8601 parsers
     */
    object Iso {
        /**
         * Parse ISO-8601 calendar dates in either basic or extended format
         */
        val CALENDAR_DATE = dateTimeParser {
            anyOf(Basic.CALENDAR_DATE, Extended.CALENDAR_DATE)
        }

        /**
         * Parse ISO-8601 ordinal dates in either basic or extended format
         */
        val ORDINAL_DATE = dateTimeParser {
            anyOf(Basic.ORDINAL_DATE, Extended.ORDINAL_DATE)
        }

        /**
         * Parse ISO-8601 calendar or ordinal dates in either basic or extended format
         */
        val DATE = dateTimeParser {
            anyOf(CALENDAR_DATE, ORDINAL_DATE)
        }

        /**
         * Parse an ISO-8601 time of day in either basic or extended format
         */
        val TIME = dateTimeParser {
            anyOf(Basic.TIME, Extended.TIME)
        }

        /**
         * Parse an ISO-8601 time shift in either basic or extended format
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

        val DATE_TIME = dateTimeParser {
            anyOf(Basic.DATE_TIME, Extended.DATE_TIME)
        }

        val OFFSET_TIME = dateTimeParser {
            anyOf(Basic.OFFSET_TIME, Extended.OFFSET_TIME)
        }

        val OFFSET_DATE_TIME = dateTimeParser {
            anyOf(Basic.OFFSET_DATE_TIME, Extended.OFFSET_DATE_TIME)
        }

        val ZONED_DATE_TIME = dateTimeParser {
            anyOf(Basic.ZONED_DATE_TIME, Extended.ZONED_DATE_TIME)
        }

        /**
         * Parse an ISO-8601 date-time with the zero UTC offset designator in either basic or extended format.
         *
         * Examples:
         * - 2001-05-10T00:24:00.00000Z
         * - 2001-05-10T00:24Z
         * - 20010510 0024Z
         */
        val INSTANT = dateTimeParser {
            anyOf(Basic.INSTANT, Extended.INSTANT)
        }

        /**
         * Parse an ISO-8601 year-month.
         *
         * The standard supports only extended format.
         */
        val YEAR_MONTH = Extended.YEAR_MONTH

        /**
         * Parse an ISO-8601 year.
         *
         * Only 4-digit years are currently supported.
         */
        val YEAR = dateTimeParser {
            year(4) { enforceSignStyle(SignStyle.NEVER) }
        }

        /**
         * Parse an ISO-8601 period without any time components
         */
        val PERIOD = dateTimeParser {
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
                periodOfDays()
                +'D'
            }
        }

        /**
         * Parse an ISO-8601 period containing only the day and time components
         */
        val DURATION = dateTimeParser {
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
                    enforceSignStyle(SignStyle.NEVER)
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

            val DATE_TIME = dateTimeParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = dateTimeParser {
                childParser(TIME)
                childParser(UTC_OFFSET)
            }

            val OFFSET_DATE_TIME = dateTimeParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
            }

            val ZONED_DATE_TIME = dateTimeParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
                optional {
                    +'['
                    timeZoneId()
                    +']'
                }
            }

            val INSTANT = dateTimeParser {
                childParser(DATE_TIME)
                utcDesignator()
            }

            val DATE_RANGE = buildIsoIntervalParser(CALENDAR_DATE)
            val DATE_TIME_INTERVAL = buildIsoIntervalParser(DATE_TIME)
            val OFFSET_DATE_TIME_INTERVAL = buildIsoIntervalParser(OFFSET_DATE_TIME)
            val ZONED_DATE_TIME_INTERVAL = buildIsoIntervalParser(ZONED_DATE_TIME)
            val INSTANT_INTERVAL = buildIsoIntervalParser(INSTANT)
        }

        object Extended {
            val CALENDAR_DATE = dateTimeParser {
                year(4) {
                    enforceSignStyle(SignStyle.NEVER)
                }
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

            val DATE_TIME = dateTimeParser {
                childParser(CALENDAR_DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(TIME)
            }

            val OFFSET_TIME = dateTimeParser {
                childParser(TIME)
                childParser(UTC_OFFSET)
            }

            val OFFSET_DATE_TIME = dateTimeParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
            }

            val ZONED_DATE_TIME = dateTimeParser {
                childParser(DATE_TIME)
                childParser(UTC_OFFSET)
                optional {
                    +'['
                    timeZoneId()
                    +']'
                }
            }

            val INSTANT = dateTimeParser {
                childParser(DATE_TIME)
                utcDesignator()
            }

            val YEAR_MONTH = dateTimeParser {
                year(4) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                +'-'
                monthNumber(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
            }

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
            groups(2) {}
        })
    }
}