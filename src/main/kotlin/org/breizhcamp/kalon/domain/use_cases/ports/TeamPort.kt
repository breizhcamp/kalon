package org.breizhcamp.kalon.domain.use_cases.ports

import org.breizhcamp.kalon.application.requests.TeamCreationReq
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.entities.TeamPartial
import java.util.*

interface TeamPort {

    fun list(filter: TeamFilter): List<Team>
    fun getById(id: UUID): Optional<Team>
    fun add(creationReq: TeamCreationReq): Team
    fun update(id: UUID, team: TeamPartial): Team
    fun existsById(id: UUID): Boolean

}