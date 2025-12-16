package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.infrastructure.db.model.EventDB

fun Event.toDb(): EventDB {
    return EventDB(
        id = this.id.value,
        name = this.name,
        startDate = this.startDate,
        endDate = this.endDate,
        website = this.website,
        venue = this.venue
    )
}

fun EventDB.toDomain(): Event {
    return Event(
        id = EventId(this.id),
        name = this.name,
        startDate = this.startDate,
        endDate = this.endDate,
        website = this.website,
        venue = this.venue
    )
}

