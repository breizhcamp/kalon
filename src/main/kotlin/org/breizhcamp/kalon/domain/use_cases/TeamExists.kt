package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class TeamExists (
    private val teamPort: TeamPort
) {
    fun exists(id: UUID): Boolean = teamPort.existsById(id)
}