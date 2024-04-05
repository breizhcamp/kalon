package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.breizhcamp.kalon.infrastructure.db.model.EventParticipantDB

interface EventRepoCustom {

    fun filter(filter: EventFilter): List<EventDB>
    fun getParticipants(id: Int): Set<EventParticipantDB>

}