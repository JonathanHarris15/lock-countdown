package com.jonathan.lockcountdown

import java.time.ZoneId
import java.time.ZonedDateTime

/** Change the target here. */
object Countdown {
    val TARGET: ZonedDateTime =
        ZonedDateTime.of(2027, 5, 17, 16, 30, 0, 0, ZoneId.of("America/Chicago"))

    const val TITLE = "MAY 17 · 4:30 PM"

    val targetMillis: Long get() = TARGET.toInstant().toEpochMilli()
}
