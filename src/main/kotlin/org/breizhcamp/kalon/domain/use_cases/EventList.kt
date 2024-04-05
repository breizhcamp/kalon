package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service

@Service
class EventList (
    private val eventPort: EventPort
) {

    fun list(filter: EventFilter): List<Event> = eventPort.list(filter)

}