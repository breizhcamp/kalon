package org.breizhcamp.kalon.infrastructure

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.application.requests.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.infrastructure.db.mappers.toEvent
import org.breizhcamp.kalon.infrastructure.db.mappers.toParticipant
import org.breizhcamp.kalon.infrastructure.db.repos.EventRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class EventAdapter (
    private val eventRepo: EventRepo
): EventPort {

    override fun list(filter: EventFilter): List<Event> =
        eventRepo.filter(filter).map { it.toEvent() }

    override fun add(creationReq: EventCreationReq): Event =
        eventRepo.createEvent(creationReq.name, creationReq.year).toEvent()

    override fun exists(id: Int): Boolean =
        eventRepo.existsById(id)

    override fun getById(id: Int): Optional<Event> {
        val baseEventDB = eventRepo.findById(id)

        if (baseEventDB.isEmpty) return Optional.empty()

        val participants = eventRepo.getParticipants(id)
        val event = baseEventDB.get().toEvent()
        event.eventParticipants = participants.map { it.toParticipant() }.toSet()

        return Optional.of(event)
    }

    @Transactional
    override fun update(id: Int, partial: EventPartial): Event {
        eventRepo.updateInfos(
            id = id,
            name = partial.name,
            year = partial.year,
            debutEvent = partial.debutEvent,
            finEvent = partial.finEvent,
            debutCFP = partial.debutCFP,
            finCFP = partial.finCFP,
            debutInscription = partial.debutInscription,
            finInscription = partial.finInscription,
            website = partial.website
        )

        return this.getById(id).get()
    }

    @Transactional
    override fun delete(id: Int) =
        eventRepo.deleteById(id)

}