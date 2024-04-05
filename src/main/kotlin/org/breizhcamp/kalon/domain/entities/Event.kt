package org.breizhcamp.kalon.domain.entities

import java.time.LocalDateTime

data class Event(
    val id: Int,
    val name: String?,
    val year: Int,
    val debutEvent: LocalDateTime?,
    val finEvent: LocalDateTime?,
    val debutCFP: LocalDateTime?,
    val finCFP: LocalDateTime?,
    val debutInscription: LocalDateTime?,
    val finInscription: LocalDateTime?,
    val website: String?,
    var eventParticipants: Set<EventParticipant>,
)
