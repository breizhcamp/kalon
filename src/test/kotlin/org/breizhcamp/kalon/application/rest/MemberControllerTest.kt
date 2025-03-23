package org.breizhcamp.kalon.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.application.dto.MemberDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.entities.MemberParticipation
import org.breizhcamp.kalon.domain.entities.Order
import org.breizhcamp.kalon.domain.use_cases.*
import org.breizhcamp.kalon.testUtils.*
import org.hibernate.type.descriptor.java.CoercionHelper.toLong
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(MemberController::class)
class MemberControllerTest {

    @MockkBean
    private lateinit var memberCRUD: MemberCRUD

    @MockkBean
    private lateinit var memberCreateParticipation: MemberCreateParticipation

    @MockkBean
    private lateinit var memberDeleteParticipation: MemberDeleteParticipation

    @MockkBean
    private lateinit var memberCreateContact: MemberCreateContact

    @MockkBean
    private lateinit var memberDeleteContact: MemberDeleteContact

    @MockkBean
    private lateinit var handleNotFound: HandleNotFound

    @Autowired
    private lateinit var memberController: MemberController

    @Test
    fun `listAll should log, call memberList with empty filter and return a list of UUIDs`(
        output: CapturedOutput
    ) {
        val returned = listOf(
            generateRandomMember(),
            generateRandomMember(),
            generateRandomMember()
        )
        every {
            memberCRUD.list(MemberFilter.empty())
        } returns returned.sortedBy { "${it.lastname}${it.firstname}" }

        assertEquals(
            memberController.listAll(),
            returned.sortedBy { "${it.lastname}${it.firstname}" }
                .map { it.id }
        )
        assert(output.contains(
            "Listing all Members by their IDs"
        ))

        verify { memberCRUD.list(MemberFilter.empty()) }
    }

    @Test
    fun `addMember should log, call memberAdd with request and return MemberDTO`(
        output: CapturedOutput
    ) {
        val req = MemberCreationReq(generateRandomHexString(), generateRandomHexString())
        val member = generateRandomMember().copy(lastname = req.lastname, firstname = req.firstname)

        every { memberCRUD.add(req) } returns member

        assertEquals(
            memberController.addMember(req),
            member.toDto()
        )
        assert(output.contains(
            "Adding Member with values lastname=${req.lastname} firstname=${req.firstname}"
        ))

        verify { memberCRUD.add(req) }
    }

    @Test
    fun `filterMembers should log, call memberList with request and return a list of MemberDTOs`(
        output: CapturedOutput
    ) {
        val limit = Random.nextInt(1, 4)
        val members = (0..5)
            .map { generateRandomMember() }
            .sortedBy { "${it.lastname}${it.firstname}" }
            .take(limit)
        val filter = MemberFilter.empty().copy(limit = toLong(limit))

        every { memberCRUD.list(filter) } returns members

        assertEquals(
            memberController.filterMembers(filter),
            members.take(limit).map { it.toDto() }
        )
        assert(output.contains("Filtering $limit Members with ${Order.ASC} name order"))

        verify { memberCRUD.list(filter) }
    }

    @Test
    fun `getById should log, call handleNotFound and memberGet and return OK and the MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val member = generateRandomMember()

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { memberCRUD.getById(member.id) } returns Optional.of(member)

        assertEquals(memberController.getById(member.id), ResponseEntity.ok(member.toDto()))
        assert(output.contains(
            "Retrieving Member:${member.id}"
        ))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { memberCRUD.getById(member.id) }
    }

    @Test
    fun `getById should log, call handleNotFound, not call memberGet and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()

        every { handleNotFound.memberNotFound(id) } returns true
        every { memberCRUD.getById(id) } returns Optional.empty()

        assertEquals(memberController.getById(id), ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Retrieving Member:$id"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberCRUD.getById(id) wasNot Called}
    }

    @Test
    fun `update should log, call handleNotFound and memberUpdate and return OK and MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val lastname = generateRandomHexString()
        val partial = MemberPartial.empty().copy(lastname = lastname)
        val member = generateRandomMember().copy(lastname = lastname)

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { memberCRUD.update(member.id, partial) } returns member

        assertEquals(memberController.update(member.id, partial.toDto()), ResponseEntity.ok(member.toDto()))
        assert(output.contains(
            "Updating Member:${member.id} with values lastname=${partial.lastname} firstname=${partial.firstname} and profilePictureLink=${partial.profilePictureLink}"
        ))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { memberCRUD.update(member.id, partial) }
    }

    @Test
    fun `update should log, call handleNotFound, not call memberUpdate and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val partial = MemberPartial.empty()

        every { handleNotFound.memberNotFound(any()) } returns true
        every { memberCRUD.update(any(), any()) }

        assertEquals(memberController.update(id, partial.toDto()), ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Updating Member:$id with values lastname=${partial.lastname} firstname=${partial.firstname} and profilePictureLink=${partial.profilePictureLink}"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberCRUD.update(id, partial) wasNot Called }
    }

    @Test
    fun `addContact should log, call handleNotFound and memberAddContact and return OK and the MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val contact = generateRandomContact()
        val member = generateRandomMember().copy(contacts = setOf(contact))

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { memberCreateContact.create(member.id, contact.platform, contact.link) } returns member

        assertEquals(memberController.addContact(member.id, contact.platform, contact.link), ResponseEntity.ok(member.toDto()))
        assert(output.contains(
            "Adding Contact with values platform=${contact.platform} link=${contact.link} to Member:${member.id}"
        ))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { memberCreateContact.create(member.id, contact.platform, contact.link) }
    }

    @Test
    fun `addContact should log, call handleNotFound, not call memberAddContact and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val contact = generateRandomContact()
        val id = UUID.randomUUID()

        every { handleNotFound.memberNotFound(id) } returns true
        every { memberCreateContact.create(any(), any(), any()) }

        assertEquals(
            memberController.addContact(id, contact.platform, contact.link),
            ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404))
        )
        assert(output.contains(
            "Adding Contact with values platform=${contact.platform} link=${contact.link} to Member:${id}"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberCreateContact.create(id, contact.platform, contact.link) wasNot Called}
    }

    @Test
    fun `deleteContact should log, call handleNotFound and memberRemoveContact and return OK and the MemberDTO if all found`(
        output: CapturedOutput
    ) {
        val contact = generateRandomContact()
        val member = generateRandomMember().copy(contacts = setOf(contact))

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { handleNotFound.contactNotFound(member.id, contact.id) } returns false
        every { memberDeleteContact.delete(member.id, contact.id) } returns member

        val response = memberController.deleteContact(member.id, contact.id)
        assert(output.contains("Removing Contact:${contact.id} from Member:${member.id}"))
        assertEquals(response, ResponseEntity.ok(member.toDto()))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { handleNotFound.contactNotFound(member.id, contact.id) }
        verify { memberDeleteContact.delete(member.id, contact.id) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1])
    fun `deleteContact should log, call handleNotFound, not call memberRemoveContact and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val contact = generateRandomContact()
        val member = generateRandomMember().copy(contacts = setOf(contact))

        every { handleNotFound.memberNotFound(member.id) } returns (index == 0)
        every { handleNotFound.contactNotFound(member.id, contact.id) } returns (index==1)
        every { memberDeleteContact.delete(member.id, contact.id) } returns member

        val response = memberController.deleteContact(member.id, contact.id)
        assert(output.contains("Removing Contact:${contact.id} from Member:${member.id}"))
        assertEquals(response, ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))

        verify { handleNotFound.memberNotFound(member.id) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.contactNotFound(member.id, contact.id) }
        verify { memberDeleteContact.delete(member.id, contact.id) wasNot Called }
    }

    @Test
    fun `addParticipation should log, call handleNotFound and memberAddParticipation and return OK and the MemberDTO if all found`(
        output: CapturedOutput
    ) {
        val team = generateRandomTeam()
        val event = generateRandomEvent()

        val member = generateRandomMember().copy(participations = setOf(MemberParticipation(team, event)))

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { handleNotFound.teamNotFound(team.id) } returns false
        every { handleNotFound.eventNotFound(event.id) } returns false
        every { memberCreateParticipation.createParticipation(member.id, team.id, event.id) } returns member

        val response = memberController.addParticipation(member.id, event.id, team.id)
        assert(output.contains(
            "Adding Participation:<Event:${event.id}, Team:${team.id}> to Member:${member.id}"
        ))
        assertEquals(response, ResponseEntity.ok(member.toDto()))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { handleNotFound.teamNotFound(team.id) }
        verify { handleNotFound.eventNotFound(event.id) }
        verify { memberCreateParticipation.createParticipation(member.id, team.id, event.id) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `addParticipation should log, call handleNotFound, not call memberAddParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        every { handleNotFound.memberNotFound(id) } returns (index==0)
        every { handleNotFound.teamNotFound(teamId) } returns (index==1)
        every { handleNotFound.eventNotFound(eventId) } returns (index==2)
        every { memberCreateParticipation.createParticipation(id, teamId, eventId) }

        val response = memberController.addParticipation(id, eventId, teamId)
        assert(output.contains(
            "Adding Participation:<Event:$eventId, Team:$teamId> to Member:$id"
        ))
        assertEquals(response, ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))

        verify { handleNotFound.memberNotFound(id) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(teamId) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(eventId) }
        verify { memberCreateParticipation.createParticipation(id, teamId, eventId) wasNot Called }
    }

    @Test
    fun `removeParticipation should log, call handleNotFound and memberDeleteParticipation and return OK and the MemberDTO if all found`(
        output: CapturedOutput
    ) {
        val teamId = UUID.randomUUID()
        val eventId = 1

        val member = generateRandomMember()

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { handleNotFound.teamNotFound(teamId) } returns false
        every { handleNotFound.eventNotFound(eventId) } returns false
        every { handleNotFound.participationNotFound(teamId, member.id, eventId) } returns false
        every { memberDeleteParticipation.deleteParticipation(member.id, teamId, eventId) } returns member

        val response = memberController.removeParticipation(member.id, eventId, teamId)
        assert(output.contains(
            "Removing Participation:<Event:$eventId, Team:$teamId> from Member:${member.id}"
        ))
        assertEquals(response, ResponseEntity.ok(member.toDto()))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { handleNotFound.teamNotFound(teamId) }
        verify { handleNotFound.eventNotFound(eventId) }
        verify { handleNotFound.participationNotFound(teamId, member.id, eventId) }
        verify { memberDeleteParticipation.deleteParticipation(member.id, teamId, eventId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun `removeParticipation should log, call handleNotFound, not call memberDeleteParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        every { handleNotFound.memberNotFound(id) } returns (index==0)
        every { handleNotFound.teamNotFound(teamId) } returns (index==1)
        every { handleNotFound.eventNotFound(eventId) } returns (index==2)
        every { handleNotFound.participationNotFound(teamId, id, eventId) } returns (index==3)
        every { memberDeleteParticipation.deleteParticipation(id, teamId, eventId) }

        val response = memberController.removeParticipation(id, eventId, teamId)
        assert(output.contains(
            "Removing Participation:<Event:$eventId, Team:$teamId> from Member:$id"
        ))
        assertEquals(response, ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))

        verify { handleNotFound.memberNotFound(id) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(teamId) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(eventId) }
        verify(exactly = if (index <= 2) 0 else 1) { handleNotFound.participationNotFound(teamId, id, eventId) }
        verify { memberDeleteParticipation.deleteParticipation(id, teamId, eventId) wasNot Called }
    }
}