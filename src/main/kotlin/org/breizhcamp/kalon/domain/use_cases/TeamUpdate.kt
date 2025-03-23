package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class TeamUpdate (
    private val teamPort: TeamPort
) {
    fun update(id: UUID, team: TeamPartial): Team = teamPort.update(id, team)
}