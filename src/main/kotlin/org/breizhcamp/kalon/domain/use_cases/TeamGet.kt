package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class TeamGet (
    private val teamPort: TeamPort
) {
    fun getById(id: UUID): Optional<Team> = teamPort.getById(id)
}