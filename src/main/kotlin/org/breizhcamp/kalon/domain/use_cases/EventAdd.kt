package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service

@Service
class EventAdd (
    private val eventPort: EventPort
) {
    fun add(eventPartial: EventCreationReq): Event = eventPort.add(eventPartial)
}