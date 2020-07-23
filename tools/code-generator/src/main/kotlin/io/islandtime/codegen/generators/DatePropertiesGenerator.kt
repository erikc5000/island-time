package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import io.islandtime.codegen.SingleFileGenerator
import io.islandtime.codegen.calendar
import io.islandtime.codegen.descriptions.DateTimeDescription
import io.islandtime.codegen.descriptions.DateTimeDescription.Date
import io.islandtime.codegen.dsl.file
import io.islandtime.codegen.internal
import io.islandtime.codegen.measures

object DatePropertiesGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildDatePropertiesFile()
}

private fun buildDatePropertiesFile() = file(
    packageName = "io.islandtime",
    fileName = "_DateProperties",
    jvmName = "DateTimesKt"
) {
    DateTimeDescription.values().filter { it.isDateBased }.forEach { receiverClass ->
        property(name = "weekOfMonth", returnType = Int::class) {
            kdoc { "The week of the month (0-5) according to the ISO definition." }
            receiver(receiverClass.typeName)

            if (receiverClass == Date) {
                getter {
                    using("impl", internal("weekOfMonthImpl"))
                    "return %impl:T"
                }
            } else {
                modifiers(KModifier.INLINE)
                delegatesTo(receiverClass.datePropertyName)
            }
        }

        function(name = "weekOfMonth") {
            kdoc { "The week of the month (0-5) according to the week definition in [settings]." }
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

        property(name = "weekOfYear", returnType = Int::class) {
            kdoc {
                """
                    The week of the year according to the ISO week definition. If the week number is associated with the
                    preceding year, `0` will be returned.

                    To obtain the week number used in the ISO week date system, use [weekOfWeekBasedYear] instead.
 
                    @see weekOfWeekBasedYear
                """.trimIndent()
            }

            receiver(receiverClass.typeName)

            if (receiverClass == Date) {
                getter {
                    using("impl", internal("weekOfYearImpl"))
                    "return %impl:T"
                }
            } else {
                modifiers(KModifier.INLINE)
                delegatesTo(receiverClass.datePropertyName)
            }
        }

        function("weekOfYear") {
            kdoc {
                """
                    The week of the year according to the week definition in [settings]. If the week number is
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

        property(name = "weekBasedYear", returnType = Int::class) {
            kdoc {
                """
                    The week-based year used in the ISO [week·date·system](https://en.wikipedia.org/wiki/ISO_week_date).
                    This differs from the regular ISO year when the week number falls in the preceding or following
                    year.
                    
                    @see weekOfWeekBasedYear
                """.trimIndent()
            }

            receiver(receiverClass.typeName)

            if (receiverClass == Date) {
                getter {
                    using("impl", internal("weekBasedYearImpl"))
                    "return %impl:T"
                }
            } else {
                modifiers(KModifier.INLINE)
                delegatesTo(receiverClass.datePropertyName)
            }
        }

        function(name = "weekBasedYear") {
            kdoc {
                """
                    The week-based year according to the week definition in [settings]. This differs from the regular
                    ISO year when the week number falls in the preceding or following year.
                    
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
                    "return %impl:T"
                }
            } else {
                modifiers(KModifier.INLINE)
                delegatesTo(receiverClass.datePropertyName)
            }
        }

        function(name = "weekOfWeekBasedYear") {
            kdoc {
                """
                    The week number of the week-based year according to the week definition in [settings].
                    
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

        property(name = "lengthOfWeekBasedYear", returnType = measures("IntWeeks")) {
            kdoc {
                """
                    The length of the ISO week-based year that this ${receiverClass.simpleName} falls in. This will be
                    either 52 or 53 weeks.
                """.trimIndent()
            }

            receiver(receiverClass.typeName)

            if (receiverClass == Date) {
                getter {
                    using("impl", internal("lengthOfWeekBasedYearImpl"))
                    "return %impl:T"
                }
            } else {
                modifiers(KModifier.INLINE)
                delegatesTo(receiverClass.datePropertyName)
            }
        }
    }
}