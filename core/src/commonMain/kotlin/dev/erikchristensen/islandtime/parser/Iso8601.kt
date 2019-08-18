package dev.erikchristensen.islandtime.parser

object Iso8601 {
    val DATE_PARSER = dateTimeParser {
        anyOf(Basic.DATE_PARSER, Extended.DATE_PARSER)
    }

    val ORDINAL_DATE_PARSER = dateTimeParser {
        anyOf(Basic.ORDINAL_DATE_PARSER, Extended.ORDINAL_DATE_PARSER)
    }

    val TIME_PARSER = dateTimeParser {
        anyOf(Basic.TIME_PARSER, Extended.TIME_PARSER)
    }

    val TIME_OFFSET_PARSER = dateTimeParser {
        anyOf({
            timeOffsetUtc()
        }, {
            timeOffsetSign()
            timeOffsetHours(2) {
                enforceSignStyle(SignStyle.NEVER)
            }
            optional {
                anyOf({
                    timeOffsetMinutes(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        timeOffsetSeconds(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                }, {
                    +':'
                    timeOffsetMinutes(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        +':'
                        timeOffsetSeconds(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                })
            }
        })
    }

    val DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.DATE_TIME_PARSER, Extended.DATE_TIME_PARSER)
    }

    val OFFSET_DATE_TIME_PARSER = dateTimeParser {
        anyOf(Basic.OFFSET_DATE_TIME_PARSER, Extended.OFFSET_DATE_TIME_PARSER)
    }

    object Basic {
        val DATE_PARSER = dateTimeParser {
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

        val ORDINAL_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            dayOfYear(3) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        val TIME_PARSER = dateTimeParser {
            hourOfDay(2)
            optional {
                minuteOfHour(2)
                optional {
                    secondOfMinute(2)
                }
            }
        }

        val TIME_OFFSET_PARSER = dateTimeParser {
            anyOf({
                timeOffsetUtc()
            }, {
                timeOffsetSign()
                timeOffsetHours(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    timeOffsetMinutes(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        timeOffsetSeconds(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                }
            })
        }

        val DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val OFFSET_DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_TIME_PARSER)
            subParser(TIME_OFFSET_PARSER)
        }
    }

    object Extended {
        val DATE_PARSER = dateTimeParser {
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

        val ORDINAL_DATE_PARSER = dateTimeParser {
            year(4) {
                enforceSignStyle(SignStyle.NEVER)
            }
            +'-'
            dayOfYear(3) {
                enforceSignStyle(SignStyle.NEVER)
            }
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
                }
            }
        }

        val TIME_OFFSET_PARSER = dateTimeParser {
            anyOf({
                timeOffsetUtc()
            }, {
                timeOffsetSign()
                timeOffsetHours(2) {
                    enforceSignStyle(SignStyle.NEVER)
                }
                optional {
                    +':'
                    timeOffsetMinutes(2) {
                        enforceSignStyle(SignStyle.NEVER)
                    }
                    optional {
                        +':'
                        timeOffsetSeconds(2) {
                            enforceSignStyle(SignStyle.NEVER)
                        }
                    }
                }
            })
        }

        val DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_PARSER)
            anyOf({ +'T' }, { +' ' })
            subParser(TIME_PARSER)
        }

        val OFFSET_DATE_TIME_PARSER = dateTimeParser {
            subParser(DATE_TIME_PARSER)
            subParser(TIME_OFFSET_PARSER)
        }
    }
}