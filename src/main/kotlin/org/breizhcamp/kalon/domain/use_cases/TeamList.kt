package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service

@Service
class TeamList (
    private val teamPort: TeamPort
) {
    fun list(filter: TeamFilter): List<Team> = teamPort.list(filter)
}