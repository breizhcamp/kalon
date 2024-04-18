package org.breizhcamp.kalon.domain.entities

import java.time.LocalDate

data class Event(
    val id: Int,
    val name: String?,
    val year: Int,
    val debutEvent: LocalDate?,
    val finEvent: LocalDate?,
    val debutCFP: LocalDate?,
    val finCFP: LocalDate?,
    val debutInscription: LocalDate?,
    val finInscription: LocalDate?,
    val website: String?,
    var eventParticipants: Set<EventParticipant>,
)
