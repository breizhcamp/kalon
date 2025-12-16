package org.breizhcamp.kalon.domain.exceptions

import org.breizhcamp.kalon.domain.entities.EventId

class EventIdAlreadyExistsException(eventId: EventId): RuntimeException("Event with id [${eventId.value}] already exists")
