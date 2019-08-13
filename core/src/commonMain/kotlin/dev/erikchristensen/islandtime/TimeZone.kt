package dev.erikchristensen.islandtime

import kotlin.jvm.JvmField

data class TimeZone(val regionId: String) {
    companion object {
        @JvmField
        val UTC = TimeZone("Etc/UTC")
    }
}