package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.application.requests.TeamCreationReq
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service

@Service
@Transactional
class TeamAdd (
    private val teamPort: TeamPort
) {
    fun add(creationReq: TeamCreationReq): Team = teamPort.add(creationReq)
}