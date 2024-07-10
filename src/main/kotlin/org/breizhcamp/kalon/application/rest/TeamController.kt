package org.breizhcamp.kalon.application.rest

import mu.KotlinLogging
import org.breizhcamp.kalon.application.requests.TeamCreationReq
import org.breizhcamp.kalon.application.dto.TeamDTO
import org.breizhcamp.kalon.application.dto.TeamPartialDTO
import org.breizhcamp.kalon.application.dto.TeamParticipationDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.domain.use_cases.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/teams")
class TeamController (
    private val teamList: TeamList,
    private val teamAdd: TeamAdd,
    private val teamCreateParticipation: TeamCreateParticipation,
    private val teamDeleteParticipation: TeamDeleteParticipation,
    private val teamGet: TeamGet,
    private val teamUpdate: TeamUpdate,
    private val handleNotFound: HandleNotFound,
) {

    @GetMapping
    fun list(): List<UUID> {
        logger.info { "Listing all Teams by their IDs" }

        return teamList.list(TeamFilter.empty()).map { it.id }
    }

    @PostMapping
    fun addTeam(@RequestBody creationReq: TeamCreationReq): TeamDTO {
        logger.info { "Adding Team with values name=${creationReq.name}" }

        return teamAdd.add(creationReq).toDto()
    }

    @PostMapping("/filter")
    fun filter(@RequestBody filter: TeamFilter): List<TeamDTO> {
        logger.info { "Filtering Teams by memberId=${filter.memberId} eventId=${filter.eventId}" }

        return teamList.list(filter).map { it.toDto() }
    }

    @GetMapping("/{id}")
    fun getTeamInfos(@PathVariable id: UUID): ResponseEntity<TeamDTO> {
        logger.info { "Retrieving Team:$id" }

        if (handleNotFound.teamNotFound(id)) return ResponseEntity.notFound().build()

        return ResponseEntity.ok(teamGet.getById(id).get().toDto())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody team: TeamPartialDTO): ResponseEntity<TeamDTO> {
        logger.info { "Updating Team:$id with values name=${team.name} description=${team.description}" }

        if (handleNotFound.teamNotFound(id)) return ResponseEntity.notFound().build()

        return ResponseEntity.ok(teamUpdate.update(id, team.toObject()).toDto())
    }

    @PostMapping("/{id}/participation/{memberId}/{eventId}")
    fun addParticipant(
        @PathVariable id: UUID,
        @PathVariable memberId: UUID,
        @PathVariable eventId: Int
    ): ResponseEntity<TeamDTO> {
        logger.info { "Adding Participation:<Member:$memberId, Event:$eventId> to Team:$id" }

        if (handleNotFound.memberNotFound(memberId)
            || handleNotFound.teamNotFound(id)
            || handleNotFound.eventNotFound(eventId))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(teamCreateParticipation.createParticipation(id, memberId, eventId).toDto())
    }

    @DeleteMapping("/{id}/participation/{memberId}/{eventId}")
    fun removeParticipant(
        @PathVariable id: UUID,
        @PathVariable memberId: UUID,
        @PathVariable eventId: Int
    ): ResponseEntity<TeamDTO> {
        logger.info { "Removing Participant:<Member:$memberId, Event:$eventId> from Team:$id" }

        if (handleNotFound.memberNotFound(memberId)
            || handleNotFound.teamNotFound(id)
            || handleNotFound.eventNotFound(eventId)
            || handleNotFound.participationNotFound(id, memberId, eventId))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(teamDeleteParticipation.removeParticipation(id, memberId, eventId).toDto())
    }

}

fun Team.toDto() = TeamDTO(
    id = id,
    name = name,
    description = description,
    participations = participations.map { it.toDto() }
)

fun TeamPartialDTO.toObject() = TeamPartial(
    name = name,
    description = description,
)

fun TeamParticipation.toDto() = TeamParticipationDTO(
    memberId = member.id,
    eventId = event.id,
)