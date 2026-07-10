package org.breizhcamp.kalon.domain.ports

import org.breizhcamp.kalon.domain.entities.EventId

interface AppConfigPort {
    fun setDefaultEvent(eventId: EventId)
}
