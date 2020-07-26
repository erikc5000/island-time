package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import io.islandtime.codegen.*
import io.islandtime.codegen.descriptions.DateTimeDescription
import io.islandtime.codegen.descriptions.DateTimeDescription.Date
import io.islandtime.codegen.descriptions.TemporalUnitDescription.DAYS
import io.islandtime.codegen.dsl.CodeBlockBuilder
import io.islandtime.codegen.dsl.FileBuilder
import io.islandtime.codegen.dsl.file

object DatePropertiesGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildDatePropertiesFile()
}

private fun buildDatePropertiesFile() = file(
    packageName = "io.islandtime",
    fileName = "_DateProperties",
    jvmName = "DateTimesKt"
) {
    DateTimeDescription.values()
        .filter { it.isDateBased && it.smallestUnit <= DAYS }
        .forEach { buildDatePropertiesForClass(it) }
}

private fun FileBuilder.buildDatePropertiesForClass(receiverClass: DateTimeDescription) {
    receiverClass.interval?.let { intervalClass ->
        fun CodeBlockBuilder.intervalOfWeekCode(vararg startOfWeekArgs: String): String {
            using("startOfWeek", operators("startOfWeek"))
            using("days", measures("days"))

            return buildString {
                append("return %startOfWeek:T")

                if (startOfWeekArgs.isNotEmpty()) {
                    append("(${startOfWeekArgs.joinToString()})")
                }

                if (intervalClass.isInclusive) {
                    append(".let·{·it..it·+·6.%days:T·}")
                } else {
                    using("until", ranges("until"))
                    append(".let·{·it·%until:T·it·+·7.%days:T·}")
                }
            }
        }

        property(name = "week", returnType = intervalClass.typeName) {
            kdoc {
                """
                    The ${intervalClass.simpleName} defining the ISO week that this ${receiverClass.simpleName} falls
                    within.

                    The ISO week starts on Monday and ends on Sunday.
                """.trimIndent()
            }

            receiver(receiverClass.typeName)
            getter { intervalOfWeekCode() }
        }

        function(name = "week") {
            kdoc {
                """
                    The ${intervalClass.simpleName} defining the week that this ${receiverClass.simpleName} falls
                    within. The first day of the week will be determined by the provided [settings].
                """.trimIndent()
            }

            receiver(receiverClass.typeName)
            argument("settings", calendar("WeekSettings"))
            returns(intervalClass.typeName)
            code { intervalOfWeekCode("settings") }
        }

        function(name = "week") {
            kdoc {
                """
                    The ${intervalClass.simpleName} defining the week that this ${receiverClass.simpleName} falls
                    within. The first day of the week will be the default associated with the provided [locale].

                    Keep in mind that that the system's calendar settings may differ from that of the default locale on
                    some platforms. To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
                """.trimIndent()
            }

            receiver(receiverClass.typeName)
            argument("locale", locale("Locale"))
            returns(intervalClass.typeName)
            code { intervalOfWeekCode("locale") }
        }
    }

    property(name = "weekOfMonth", returnType = Int::class) {
        kdoc { "The week of the month, from 0-6, calculated using the ISO week definition." }
        receiver(receiverClass.typeName)

        if (receiverClass == Date) {
            getter {
                using("impl", internal("weekOfMonthImpl"))
                using("weekSettings", calendar("WeekSettings"))
                "return %impl:T(%weekSettings:T.ISO)"
            }
        } else {
            modifiers(KModifier.INLINE)
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekOfMonth") {
        kdoc { "The week of the month, from 0-6, calculated using the week definition in [settings]." }
        receiver(receiverClass.typeName)
        argument("settings", calendar("WeekSettings"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfMonthImpl"))
                "return %impl:T(%settings:N)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekOfMonth") {
        kdoc {
            """
                The week of the month, from 0-6, calculated using the default week definition associated with the
                provided [locale].
                
                Keep in mind that that the system's calendar settings may differ from that of the default locale on
                some platforms. To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("locale", locale("Locale"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfMonthImpl"))
                using("weekSettings", calendar("weekSettings"))
                "return %impl:T(%locale:N.%weekSettings:T)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    property(name = "weekOfYear", returnType = Int::class) {
        kdoc {
            """
                The week of the year, calculated using the ISO week definition. If the week number is associated with
                the preceding year, `0` will be returned.

                To obtain the week number used in the ISO week date system, use [weekOfWeekBasedYear] instead.

                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)

        if (receiverClass == Date) {
            getter {
                using("impl", internal("weekOfYearImpl"))
                using("weekSettings", calendar("WeekSettings"))
                "return %impl:T(%weekSettings:T.ISO)"
            }
        } else {
            modifiers(KModifier.INLINE)
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function("weekOfYear") {
        kdoc {
            """
                The week of the year, calculated using the week definition in [settings]. If the week number is
                associated with the preceding year, `0` will be returned.

                To obtain the week number of the week-based year, use [weekOfWeekBasedYear] instead.
                
                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("settings", calendar("WeekSettings"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfYearImpl"))
                "return %impl:T(%settings:N)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function("weekOfYear") {
        kdoc {
            """
                The week of the year, calculated using the week definition associated with the provided [locale]. If the
                week number is associated with the preceding year, `0` will be returned.

                To obtain the week number of the week-based year, use [weekOfWeekBasedYear] instead.
                
                Keep in mind that that the system's calendar settings may differ from that of the default locale on
                some platforms. To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
                
                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("locale", locale("Locale"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfYearImpl"))
                using("weekSettings", calendar("weekSettings"))
                "return %impl:T(%locale:N.%weekSettings:T)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    property(name = "weekBasedYear", returnType = Int::class) {
        kdoc {
            """
                The week-based year used in the ISO [week·date·system](https://en.wikipedia.org/wiki/ISO_week_date).
                This value differs from the regular ISO year when the week number falls in the preceding or following
                year.
                
                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)

        if (receiverClass == Date) {
            getter {
                using("impl", internal("weekBasedYearImpl"))
                using("weekSettings", calendar("WeekSettings"))
                "return %impl:T(%weekSettings:T.ISO)"
            }
        } else {
            modifiers(KModifier.INLINE)
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekBasedYear") {
        kdoc {
            """
                The week-based year, calculated using the week definition in [settings]. This value differs from the
                regular ISO year when the week number falls in the preceding or following year.
                
                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("settings", calendar("WeekSettings"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekBasedYearImpl"))
                "return %impl:T(%settings:N)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekBasedYear") {
        kdoc {
            """
                The week-based year, calculated using the week definition associated with the provided [locale]. This
                value differs from the regular ISO year when the week number falls in the preceding or following year.
                
                Keep in mind that that the system's calendar settings may differ from that of the default locale on
                some platforms. To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
                
                @see weekOfWeekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("locale", locale("Locale"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekBasedYearImpl"))
                using("weekSettings", calendar("weekSettings"))
                "return %impl:T(%locale:N.%weekSettings:T)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    property(name = "weekOfWeekBasedYear", returnType = Int::class) {
        kdoc {
            """
                The week number used in the ISO [week·date·system](https://en.wikipedia.org/wiki/ISO_week_date).
                
                @see weekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)

        if (receiverClass == Date) {
            getter {
                using("impl", internal("weekOfWeekBasedYearImpl"))
                using("weekSettings", calendar("WeekSettings"))
                "return %impl:T(%weekSettings:T.ISO)"
            }
        } else {
            modifiers(KModifier.INLINE)
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekOfWeekBasedYear") {
        kdoc {
            """
                The week number of the week-based year, calculated using the week definition in [settings].
                
                @see weekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("settings", calendar("WeekSettings"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfWeekBasedYearImpl"))
                "return %impl:T(%settings:N)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    function(name = "weekOfWeekBasedYear") {
        kdoc {
            """
                The week number of the week-based year, calculated using the week definition associated with the
                provided [locale].
                
                Keep in mind that that the system's calendar settings may differ from that of the default locale on
                some platforms. To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
                
                @see weekBasedYear
            """.trimIndent()
        }

        receiver(receiverClass.typeName)
        argument("locale", locale("Locale"))
        returns(Int::class)

        if (receiverClass == Date) {
            code {
                using("impl", internal("weekOfWeekBasedYearImpl"))
                using("weekSettings", calendar("weekSettings"))
                "return %impl:T(%locale:N.%weekSettings:T)"
            }
        } else {
            delegatesTo(receiverClass.datePropertyName)
        }
    }

    property(name = "lengthOfWeekBasedYear", returnType = measures("IntWeeks")) {
        kdoc {
            """
                The length of the ISO week-based year that this ${receiverClass.simpleName} falls in, either 52 or 53
                weeks.
            """.trimIndent()
        }

        receiver(receiverClass.typeName)

        if (receiverClass == Date) {
            getter {
                using("impl", internal("lengthOfWeekBasedYear"))
                "return %impl:T(weekBasedYear)"
            }
        } else {
            modifiers(KModifier.INLINE)
            delegatesTo(receiverClass.datePropertyName)
        }
    }
}