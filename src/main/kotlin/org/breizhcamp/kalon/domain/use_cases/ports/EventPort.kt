package org.breizhcamp.kalon.domain.use_cases.ports

import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import java.util.*

interface EventPort {

    fun list(filter: EventFilter): List<Event>
    fun add(creationReq: EventCreationReq): Event
    fun exists(id: Int): Boolean
    fun getById(id: Int): Optional<Event>
    fun update(id: Int, partial: EventPartial): Event
    fun delete(id: Int)

}