package org.breizhcamp.kalon.domain.entities

import java.util.*

data class Team(
    val id: UUID,
    val name: String,
    val description: String?,
    var participations: Set<TeamParticipation>,
)
