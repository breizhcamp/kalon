package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import org.breizhcamp.kalon.infrastructure.db.model.TeamParticipationDB

fun TeamDB.toTeam() = Team(
    id = id,
    name = name,
    description = description,
    participations = emptySet()
)

fun TeamParticipationDB.toParticipation() = TeamParticipation(
    member = memberDB.toMember(),
    event = eventDB.toEvent(),
)