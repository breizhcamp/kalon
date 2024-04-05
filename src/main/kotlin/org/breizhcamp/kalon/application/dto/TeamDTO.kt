package org.breizhcamp.kalon.application.dto

import java.util.*

data class TeamDTO(
    val id: UUID,
    val name: String,
    val description: String?,
    val participations: List<TeamParticipationDTO>,
)
