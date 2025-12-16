package org.breizhcamp.kalon.infrastructure.db

import org.breizhcamp.kalon.config.annotations.Adapter
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.ports.EventPort
import org.breizhcamp.kalon.infrastructure.db.mappers.toDb
import org.breizhcamp.kalon.infrastructure.db.repos.EventRepo

@Adapter
class EventAdapter(
    private val eventRepo: EventRepo,
): EventPort {
    override fun isIdExists(eventId: EventId): Boolean {
        return eventRepo.existsById(eventId.value)
    }

    override fun create(event: Event) {
        eventRepo.save(event.toDb())
    }

    override fun update(event: Event) {
        eventRepo.save(event.toDb())
    }

    override fun delete(eventId: EventId) {
        eventRepo.deleteById(eventId.value)
    }
}
