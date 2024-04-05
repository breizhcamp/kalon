package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventGet (
    private val eventPort: EventPort
) {
    fun get(id: Int): Optional<Event> = eventPort.getById(id)
}