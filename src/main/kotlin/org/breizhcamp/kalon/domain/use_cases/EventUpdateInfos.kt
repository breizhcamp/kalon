package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service

@Service
@Transactional
class EventUpdateInfos (
    private val eventPort: EventPort
) {
    fun updateInfos(id: Int, partial: EventPartial): Event = eventPort.updateInfos(id, partial)
}