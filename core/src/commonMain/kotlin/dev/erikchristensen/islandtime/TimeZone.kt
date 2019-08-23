package dev.erikchristensen.islandtime

data class TimeZone(val regionId: String) {
    companion object {
        val UTC = TimeZone("Etc/UTC")
    }
}