package io.islandtime

fun <T : Any> objectOf(body : T.() -> Unit = { }) =
    js("new Object()")
        .unsafeCast<T>()
        .apply(body)