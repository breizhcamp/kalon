package org.breizhcamp.kalon.domain.entities

import java.time.LocalDate

@JvmInline
value class EventId(val value: String)

data class Event(
    val id: EventId,
    var name: String,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var website: String?,
    var venue: String?,
)
