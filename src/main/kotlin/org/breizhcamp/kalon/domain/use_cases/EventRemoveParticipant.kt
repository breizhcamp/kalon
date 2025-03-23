package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class EventRemoveParticipant(
    private val eventPort: EventPort,
    private val participationPort: ParticipationPort,
) {
    fun removeParticipant(id: Int, memberId: UUID, teamId: UUID): Event {
        participationPort.removeByIds(teamId, memberId, id)
        return eventPort.getById(id).get()
    }
}