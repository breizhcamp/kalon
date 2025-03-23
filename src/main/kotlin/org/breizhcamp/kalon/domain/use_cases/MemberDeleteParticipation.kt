package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberDeleteParticipation (
    private val memberPort: MemberPort,
    private val participationPort: ParticipationPort,
) {
    fun deleteParticipation(id: UUID, teamId: UUID, eventId: Int): Member {
        participationPort.removeByIds(teamId, id, eventId)
        return memberPort.getById(id).get()
    }
}