package dev.erikchristensen.islandtime.parser

object Iso8601 {
    val CALENDAR_DATE_PARSER = dateTimeParser {
        anyOf(Basic.CALENDAR_DATE_PARSER, Extended.CALENDAR_DATE_PARSER)
    }

    val ORDINAL_DATE_PARSER = dateTimeParser {
        anyOf(Basic.ORDINAL_DATE_PARSER, Extended.ORDINAL_DATE_PARSER)
    }

    /**
     * Parse ISO-8601 calendar or ordinal dates in basic or extended format
     */
    val DATE_PARSER = dateTimeParser {
        anyOf(CALENDAR_DATE_PARSER, ORDINAL_DATE_PARSER)
    }

    val TIME_PARSER = dateTimeParser {
        anyOf(Basic.TIME_PARSER, Extended.TIME_PARSER)
    }

    val UTC_OFFSET_PARSER = dateTimeParser {
        anyOf({
            utcOffsetZero()
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
                        optional {
                            decimalSeparator()
                            nanosecondOfSecond()
                        }
                    }
                })
            }
        })
    }

    val CALENDAR_DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.CALENDAR_DATE_TIME_PARSER, Extended.CALENDAR_DATE_TIME_PARSER)
    }

    val ORDINAL_DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.ORDINAL_DATE_TIME_PARSER, Extended.ORDINAL_DATE_TIME_PARSER)
    }

    val DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.DATE_TIME_PARSER, Extended.DATE_TIME_PARSER)
    }

    val OFFSET_TIME_PARSER = dateTimeParser {
        anyOf(Basic.OFFSET_TIME_PARSER, Extended.OFFSET_TIME_PARSER)
    }

    val OFFSET_DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.OFFSET_DATE_TIME_PARSER, Extended.OFFSET_DATE_TIME_PARSER)
    }

    val YEAR_MONTH_PARSER = Extended.YEAR_MONTH_PARSER

    val YEAR_PARSER = dateTimeParser {
        year(4) { enforceSignStyle(SignStyle.NEVER) }
    }

    val PERIOD_PARSER = dateTimeParser {
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

    val DURATION_PARSER = dateTimeParser {
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

    object Basic {
        val CALENDAR_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            monthOfYear(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
            dayOfMonth(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        val ORDINAL_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            dayOfYear(3) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        val DATE_PARSER = dateTimeParser {
            anyOf(CALENDAR_DATE_PARSER, ORDINAL_DATE_PARSER)
        }

        val TIME_PARSER = dateTimeParser {
            hourOfDay(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
            optional {
                minuteOfHour(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    secondOfMinute(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        decimalSeparator()
                        nanosecondOfSecond()
                    }
                }
            }
        }

        val UTC_OFFSET_PARSER = dateTimeParser {
            anyOf({
                utcOffsetZero()
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

        val CALENDAR_DATE_TIME_PARSER = dateTimeParser {
            subParser(CALENDAR_DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val ORDINAL_DATE_TIME_PARSER = dateTimeParser {
            subParser(ORDINAL_DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val OFFSET_TIME_PARSER = dateTimeParser {
            subParser(TIME_PARSER)
            subParser(UTC_OFFSET_PARSER)
        }

        val OFFSET_DATE_TIME_PARSER = dateTimeParser {
            subParser(CALENDAR_DATE_TIME_PARSER)
            subParser(UTC_OFFSET_PARSER)
        }
    }

    object Extended {
        val CALENDAR_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            +'-'
            monthOfYear(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
            +'-'
            dayOfMonth(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        val ORDINAL_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            +'-'
            dayOfYear(3) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        val DATE_PARSER = dateTimeParser {
            anyOf(CALENDAR_DATE_PARSER, ORDINAL_DATE_PARSER)
        }

        val TIME_PARSER = dateTimeParser {
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
                    secondOfMinute(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        decimalSeparator()
                        nanosecondOfSecond()
                    }
                }
            }
        }

        val UTC_OFFSET_PARSER = dateTimeParser {
            anyOf({
                utcOffsetZero()
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

        val CALENDAR_DATE_TIME_PARSER = dateTimeParser {
            subParser(CALENDAR_DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val ORDINAL_DATE_TIME_PARSER = dateTimeParser {
            subParser(ORDINAL_DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val OFFSET_TIME_PARSER = dateTimeParser {
            subParser(TIME_PARSER)
            subParser(UTC_OFFSET_PARSER)
        }

        val OFFSET_DATE_TIME_PARSER = dateTimeParser {
            subParser(CALENDAR_DATE_TIME_PARSER)
            subParser(UTC_OFFSET_PARSER)
        }

        val YEAR_MONTH_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            +'-'
            monthOfYear(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }
    }
}