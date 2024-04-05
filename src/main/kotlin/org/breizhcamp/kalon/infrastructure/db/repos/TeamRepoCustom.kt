package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import org.breizhcamp.kalon.infrastructure.db.model.TeamParticipationDB
import java.util.*

interface TeamRepoCustom {

    fun filter(filter: TeamFilter): List<TeamDB>
    fun getParticipations(id: UUID): Set<TeamParticipationDB>

}