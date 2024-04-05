package org.breizhcamp.kalon.application.dto

import java.time.LocalDateTime

data class EventDTO (
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
    val participants: List<EventParticipantDTO>,
)