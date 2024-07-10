package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.application.requests.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventCrud(
    private val eventPort: EventPort
) {
    fun create(eventPartial: EventCreationReq): Event = eventPort.add(eventPartial)
    fun exists(id: Int): Boolean = eventPort.exists(id)
    fun get(id: Int): Optional<Event> = eventPort.getById(id)
    fun list(filter: EventFilter): List<Event> = eventPort.list(filter)
    fun update(id: Int, partial: EventPartial): Event = eventPort.update(id, partial)
    fun delete(id: Int) = eventPort.delete(id)
}