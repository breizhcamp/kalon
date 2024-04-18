package org.breizhcamp.kalon.application.rest

import mu.KotlinLogging
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.application.dto.EventDTO
import org.breizhcamp.kalon.application.dto.EventPartialDTO
import org.breizhcamp.kalon.application.dto.EventParticipantDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.*
import org.breizhcamp.kalon.domain.use_cases.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/events")
class EventController (
    private val eventList: EventList,
    private val eventAdd: EventAdd,
    private val eventGet: EventGet,
    private val eventAddParticipant: EventAddParticipant,
    private val eventRemoveParticipant: EventRemoveParticipant,
    private val eventUpdateInfos: EventUpdateInfos,
    private val handleNotFound: HandleNotFound,
) {

    @GetMapping
    fun list(): List<Int> {
        logger.info { "Listing all Events by their IDs" }

        return eventList.list(EventFilter.default()).map { it.id }
    }

    @PostMapping
    fun createEvent(@RequestBody event: EventCreationReq): EventDTO {
        logger.info { "Creating Event with values name=${event.name} year=${event.year}" }

        return eventAdd.add(event).toDto()
    }

    @PostMapping("/filter")
    fun filterEvents(@RequestBody filter: EventFilter): List<EventDTO> {
        logger.info { "Filtering ${filter.limit?:"all"} Events with ${filter.yearOrder?:Order.DESC} year order" }

        return eventList.list(filter).map { it.toDto() }
    }

    @GetMapping("/{id}")
    fun getEventInfos(@PathVariable id: Int): ResponseEntity<EventDTO> {
        logger.info { "Retrieving Event:$id" }

        if (handleNotFound.eventNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(eventGet.get(id).get().toDto())
    }

    @PostMapping("/{id}")
    fun updateEventInfos(@PathVariable id: Int, @RequestBody partialDTO: EventPartialDTO): ResponseEntity<EventDTO> {
        logger.info { "Updating infos of Event:$id" }
        logger.info { "${partialDTO.debutEvent} -> ${partialDTO.finEvent}" }

        if (handleNotFound.eventNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(eventUpdateInfos.updateInfos(id, partialDTO.toObject()).toDto())
    }

    @PostMapping("/{id}/participants/{memberId}/{teamId}")
    fun addParticipant(
        @PathVariable id: Int,
        @PathVariable memberId: UUID,
        @PathVariable teamId: UUID
    ): ResponseEntity<EventDTO> {
        logger.info { "Adding Participant:<Member:$memberId, Team:$teamId> to Event:$id" }

        if (handleNotFound.memberNotFound(memberId)
            || handleNotFound.teamNotFound(teamId)
            || handleNotFound.eventNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(eventAddParticipant.addParticipant(id, memberId, teamId).toDto())
    }

    @DeleteMapping("/{id}/participants/{memberId}/{teamId}")
    fun removeParticipant(
        @PathVariable id: Int,
        @PathVariable memberId: UUID,
        @PathVariable teamId: UUID
    ): ResponseEntity<EventDTO> {
        logger.info { "Removing Participant:<Member:$memberId, Team:$teamId> from Event:$id" }

        if (handleNotFound.memberNotFound(memberId)
            || handleNotFound.teamNotFound(teamId)
            || handleNotFound.eventNotFound(id)
            || handleNotFound.participationNotFound(teamId, memberId, id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(eventRemoveParticipant.removeParticipant(id, memberId, teamId).toDto())
    }

}

fun Event.toDto() = EventDTO(
    id = id,
    name = name,
    year = year,
    debutEvent = debutEvent,
    finEvent = finEvent,
    debutCFP = debutCFP,
    finCFP = finCFP,
    debutInscription = debutInscription,
    finInscription = finInscription,
    website = website,
    participants = eventParticipants.map { it.toDto() }
)

fun EventParticipant.toDto() = EventParticipantDTO(
    teamId = team.id,
    memberId = member.id,
)

fun EventPartialDTO.toObject() = EventPartial(
    name = name,
    year = year,
    debutEvent = debutEvent,
    finEvent = finEvent,
    debutCFP = debutCFP,
    finCFP = finCFP,
    debutInscription = debutInscription,
    finInscription = finInscription,
    website = website
)

fun EventPartial.toDto() = EventPartialDTO(
    name, year, debutEvent, finEvent, debutCFP, finCFP, debutInscription, finInscription, website
)