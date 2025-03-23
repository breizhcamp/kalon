package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ParticipationExists (
    private val participationPort: ParticipationPort
) {

    fun exists(teamId: UUID, memberId: UUID, eventId: Int): Boolean = participationPort.existsByIds(teamId, memberId, eventId)

}