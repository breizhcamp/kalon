package org.breizhcamp.kalon.application.dto

import java.time.LocalDate

data class EventDTO (
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
    val participants: List<EventParticipantDTO>,
)