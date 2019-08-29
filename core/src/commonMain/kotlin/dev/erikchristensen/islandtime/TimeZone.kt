package dev.erikchristensen.islandtime

// TODO: Flush this out
data class TimeZone(val regionId: String) {
    companion object {
        val UTC = TimeZone("Etc/UTC")
    }
}