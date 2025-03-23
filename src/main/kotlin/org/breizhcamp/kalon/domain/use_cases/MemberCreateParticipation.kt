package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberCreateParticipation (
    private val memberPort: MemberPort,
    private val participationPort: ParticipationPort,
) {
    fun createParticipation(id: UUID, teamId: UUID, eventId: Int): Member {
        participationPort.createByIds(teamId, id, eventId)
        return memberPort.getById(id).get()
    }
}