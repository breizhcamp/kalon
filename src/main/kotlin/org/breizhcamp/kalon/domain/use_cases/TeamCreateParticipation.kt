package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class TeamCreateParticipation (
    private val teamPort: TeamPort,
    private val participationPort: ParticipationPort,
) {
    fun createParticipation(id: UUID, memberId: UUID, eventId: Int): Team {
        participationPort.createByIds(id, memberId, eventId)
        return teamPort.getById(id).get()
    }
}