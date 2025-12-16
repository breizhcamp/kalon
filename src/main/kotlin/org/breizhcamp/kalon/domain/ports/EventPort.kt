package org.breizhcamp.kalon.domain.ports

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId

interface EventPort {

    fun isIdExists(eventId: EventId): Boolean
    fun create(event: Event)
    fun update(event: Event)
    fun delete(eventId: EventId)
    fun list(): List<Event>

}
