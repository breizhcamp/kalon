package org.breizhcamp.kalon.application.rest

import jakarta.websocket.server.PathParam
import mu.KotlinLogging
import org.breizhcamp.kalon.application.dto.*
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.*
import org.breizhcamp.kalon.domain.use_cases.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/members")
class MemberController (
    private val memberCRUD: MemberCRUD,
    private val memberCreateParticipation: MemberCreateParticipation,
    private val memberDeleteParticipation: MemberDeleteParticipation,
    private val memberCreateContact: MemberCreateContact,
    private val memberDeleteContact: MemberDeleteContact,
    private val handleNotFound: HandleNotFound,
) {

    @GetMapping
    fun listAll(): List<UUID> {
        logger.info { "Listing all Members by their IDs" }

        return memberCRUD.list(MemberFilter.empty()).map { it.toDto().id }
    }

    @PostMapping
    fun addMember(@RequestBody creationReq: MemberCreationReq) : MemberDTO {
        logger.info { "Adding Member with values lastname=${creationReq.lastname} firstname=${creationReq.firstname}"}

        return memberCRUD.add(creationReq).toDto()
    }

    @PostMapping("/filter")
    fun filterMembers(@RequestBody filter: MemberFilter): List<MemberDTO> {
        logger.info { "Filtering ${filter.limit?:"all"} Members with ${filter.nameOrder?:Order.ASC} name order" }

        return memberCRUD.list(filter).map { it.toDto() }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<MemberDTO> {
        logger.info { "Retrieving Member:$id" }

        if (handleNotFound.memberNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberCRUD.getById(id).get().toDto())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id : UUID, @RequestBody member: MemberPartialDTO) : ResponseEntity<MemberDTO> {
        logger.info { "Updating Member:$id with values lastname=${member.lastname} firstname=${member.firstname} and profilePictureLink=${member.profilePictureLink}" }

        if (handleNotFound.memberNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberCRUD.update(id, member.toObject()).toDto())
    }

    @PostMapping("/{id}/contact")
    fun addContact(@PathVariable id: UUID, @PathParam("platform") platform: String, @PathParam("link") link: String): ResponseEntity<MemberDTO> {
        logger.info { "Adding Contact with values platform=$platform link=$link to Member:$id" }

        if (handleNotFound.memberNotFound(id))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberCreateContact.create(id, platform, link).toDto())
    }

    @DeleteMapping("/{id}/contact/{contactId}")
    fun deleteContact(@PathVariable id: UUID, @PathVariable contactId: UUID): ResponseEntity<MemberDTO> {
        logger.info { "Removing Contact:$contactId from Member:$id" }

        if (handleNotFound.memberNotFound(id)
            || handleNotFound.contactNotFound(id, contactId))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberDeleteContact.delete(id, contactId).toDto())
    }

    @PostMapping("/{id}/participation/{eventId}/{teamId}")
    fun addParticipation(
        @PathVariable id: UUID,
        @PathVariable eventId: Int,
        @PathVariable teamId: UUID
    ): ResponseEntity<MemberDTO> {
        logger.info { "Adding Participation:<Event:$eventId, Team:$teamId> to Member:$id" }

        if (handleNotFound.memberNotFound(id)
            || handleNotFound.teamNotFound(teamId)
            || handleNotFound.eventNotFound(eventId)
        )
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberCreateParticipation.createParticipation(id, teamId, eventId).toDto())
    }

    @DeleteMapping("/{id}/participation/{eventId}/{teamId}")
    fun removeParticipation(
        @PathVariable id: UUID,
        @PathVariable eventId: Int,
        @PathVariable teamId: UUID
    ): ResponseEntity<MemberDTO> {
        logger.info { "Removing Participation:<Event:$eventId, Team:$teamId> from Member:$id" }

        if (handleNotFound.memberNotFound(id)
            || handleNotFound.teamNotFound(teamId)
            || handleNotFound.eventNotFound(eventId)
            || handleNotFound.participationNotFound(teamId, id, eventId))
            return ResponseEntity.notFound().build()

        return ResponseEntity.ok(memberDeleteParticipation.deleteParticipation(id, teamId, eventId).toDto())
    }

}

fun Member.toDto() = MemberDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    contacts = contacts.map { it.toDto() },
    profilePictureLink = profilePictureLink,
    participations = participations.map { it.toDto() }
)

fun MemberPartialDTO.toObject() = MemberPartial(
    lastname = lastname,
    firstname = firstname,
    profilePictureLink = profilePictureLink
)

fun MemberPartial.toDto() = MemberPartialDTO(
    lastname, firstname, profilePictureLink
)

fun MemberParticipation.toDto() = MemberParticipationDTO(
    teamId = team.id,
    eventId = event.id,
)

fun Contact.toDto() = ContactDTO(
    id = id,
    platform = platform,
    link = link,
)
