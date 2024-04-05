package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service

@Service
class EventExists (
    private val eventPort: EventPort
) {
    fun exists(id: Int): Boolean = eventPort.exists(id)
}