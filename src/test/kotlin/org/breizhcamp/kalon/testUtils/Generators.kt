package org.breizhcamp.kalon.testUtils

import java.time.LocalDateTime
import java.time.Month
import kotlin.random.Random

fun generateRandomHexString(blocks: Int = 1): String {
    val builder = StringBuilder()

    for (i in 1..blocks) builder.append(
        Random.nextInt().toString(16)
    )

    return builder.toString()
}

fun generateRandomLocalDateTime(year: Int = 2024): LocalDateTime =
    LocalDateTime.of(
        year,
        Month.of(Random.nextInt(1, 12)),
        Random.nextInt(1, 28),
        Random.nextInt(0, 23),
        Random.nextInt(0, 59),
        Random.nextInt(0, 59)
    )


