package org.breizhcamp.kalon.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.application.dto.MemberDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.*
import org.breizhcamp.kalon.domain.use_cases.*
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

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(MemberController::class)
class MemberControllerTest {

    @MockkBean
    private lateinit var memberList: MemberList

    @MockkBean
    private lateinit var memberAdd: MemberAdd

    @MockkBean
    private lateinit var memberCreateParticipation: MemberCreateParticipation

    @MockkBean
    private lateinit var memberDeleteParticipation: MemberDeleteParticipation

    @MockkBean
    private lateinit var memberGet: MemberGet

    @MockkBean
    private lateinit var memberUpdate: MemberUpdate

    @MockkBean
    private lateinit var memberAddContact: MemberAddContact

    @MockkBean
    private lateinit var handleNotFound: HandleNotFound

    @Autowired
    private lateinit var memberController: MemberController

    @Test
    fun `listAll should log, call memberList with empty filter and return a list of UUIDs`(
        output: CapturedOutput
    ) {
        val returned = listOf(0, 1, 2).map { testMember(it) }
        every {
            memberList.list(MemberFilter.empty())
        } returns returned.sortedBy { "${it.lastname}${it.firstname}" }

        assertEquals(
            memberController.listAll(),
            returned.sortedBy { "${it.lastname}${it.firstname}" }
                .map { it.id }
        )
        assert(output.contains(
            "Listing all Members by their IDs"
        ))

        verify { memberList.list(MemberFilter.empty()) }
    }

    @Test
    fun `addMember should log, call memberAdd with request and return MemberDTO`(
        output: CapturedOutput
    ) {
        val lastname = "LUCAS"
        val firstname = "Claire"
        val id = UUID.randomUUID()

        every { memberAdd.add(MemberCreationReq(lastname, firstname)) } returns Member(
            lastname = lastname,
            firstname = firstname,
            id = id,
            contacts = emptySet(),
            participations = emptySet(),
            profilePictureLink = null
        )

        assertEquals(
            memberController.addMember(MemberCreationReq(lastname, firstname)),
            MemberDTO(id, lastname, firstname, emptyList(), null, emptyList())
        )
        assert(output.contains(
            "Adding Member with values lastname=${lastname} firstname=${firstname}"
        ))

        verify { memberAdd.add(MemberCreationReq(lastname, firstname)) }
    }

    @Test
    fun `filterMembers should log, call memberList with request and return a list of MemberDTOs`(
        output: CapturedOutput
    ) {
        val limit = 2
        val members = listOf(0, 1, 2)
            .map { testMember(it) }
            .sortedBy { "${it.lastname}${it.firstname}" }
            .take(limit)
        val filter = MemberFilter.empty().copy(limit = toLong(limit))

        every { memberList.list(filter) } returns members

        assertEquals(
            memberController.filterMembers(filter),
            members.map { it.toDto() }
        )
        assert(output.contains("Filtering $limit Members with ${Order.ASC} name order"))

        verify { memberList.list(filter) }
    }

    @Test
    fun `getById should log, call handleNotFound and memberGet and return OK and the MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val member = testMember().copy(id = id)

        every { handleNotFound.memberNotFound(id) } returns false
        every { memberGet.getById(id) } returns Optional.of(member)

        assertEquals(memberController.getById(id), ResponseEntity.ok(member.toDto()))
        assert(output.contains(
            "Retrieving Member:$id"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberGet.getById(id) }
    }

    @Test
    fun `getById should log, call handleNotFound, not call memberGet and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()

        every { handleNotFound.memberNotFound(id) } returns true
        every { memberGet.getById(id) } returns Optional.empty()

        assertEquals(memberController.getById(id), ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Retrieving Member:$id"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberGet.getById(id) wasNot Called}
    }

    @Test
    fun `update should log, call handleNotFound and memberUpdate and return OK and MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val lastname = "LUCAS-LAMMENS"
        val partial = MemberPartial.empty().copy(lastname = lastname)
        val memberAfter = testMember(0).copy(id = id, lastname = lastname)

        every { handleNotFound.memberNotFound(id) } returns false
        every { memberUpdate.update(id, partial) } returns memberAfter

        assertEquals(memberController.update(id, partial.toDto()), ResponseEntity.ok(memberAfter.toDto()))
        assert(output.contains(
            "Updating Member:$id with values lastname=${partial.lastname} firstname=${partial.firstname} and profilePictureLink=${partial.profilePictureLink}"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberUpdate.update(id, partial) }
    }

    @Test
    fun `update should log, call handleNotFound, not call memberUpdate and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val partial = MemberPartial.empty()

        every { handleNotFound.memberNotFound(any()) } returns true
        every { memberUpdate.update(any(), any()) }

        assertEquals(memberController.update(id, partial.toDto()), ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Updating Member:$id with values lastname=${partial.lastname} firstname=${partial.firstname} and profilePictureLink=${partial.profilePictureLink}"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberUpdate.update(id, partial) wasNot Called }
    }

    @Test
    fun `addContact should log, call handleNotFound and memberAddContact and return OK and the MemberDTO if Member found`(
        output: CapturedOutput
    ) {
        val contact = Contact(id = UUID.randomUUID(), platform = "Example", link = "example.org/cllucas")
        val member = testMember().copy(contacts = setOf(contact))

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { memberAddContact.addContact(member.id, contact.platform, contact.link) } returns member

        assertEquals(memberController.addContact(member.id, contact.platform, contact.link), ResponseEntity.ok(member.toDto()))
        assert(output.contains(
            "Adding Contact with values platform=${contact.platform} link=${contact.link} to Member:${member.id}"
        ))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { memberAddContact.addContact(member.id, contact.platform, contact.link) }
    }

    @Test
    fun `addContact should log, call handleNotFound, not call memberAddContact and return NOT_FOUND if Member not found`(
        output: CapturedOutput
    ) {
        val contact = Contact(id = UUID.randomUUID(), platform = "Example", link = "example.org/cllucas")
        val id = UUID.randomUUID()

        every { handleNotFound.memberNotFound(id) } returns true
        every { memberAddContact.addContact(any(), any(), any()) }

        assertEquals(
            memberController.addContact(id, contact.platform, contact.link),
            ResponseEntity<MemberDTO>(HttpStatusCode.valueOf(404))
        )
        assert(output.contains(
            "Adding Contact with values platform=${contact.platform} link=${contact.link} to Member:${id}"
        ))

        verify { handleNotFound.memberNotFound(id) }
        verify { memberAddContact.addContact(id, contact.platform, contact.link) wasNot Called}
    }

    @Test
    fun `addParticipation should log, call handleNotFound and memberAddParticipation and return OK and the MemberDTO if all found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = 1

        val team = Team(teamId, "Bureau", null, emptySet())
        val event = Event(eventId, null, 2024, null, null, null, null, null, null, null, emptySet())

        val member = testMember(1).copy(id = id, participations = setOf(MemberParticipation(team, event)))

        every { handleNotFound.memberNotFound(id) } returns false
        every { handleNotFound.teamNotFound(teamId) } returns false
        every { handleNotFound.eventNotFound(eventId) } returns false
        every { memberCreateParticipation.createParticipation(id, teamId, eventId) } returns member

        val response = memberController.addParticipation(id, eventId, teamId)
        assert(output.contains(
            "Adding Participation:<Event:$eventId, Team:$teamId> to Member:$id"
        ))
        assertEquals(response, ResponseEntity.ok(member.toDto()))

        verify { handleNotFound.memberNotFound(id) }
        verify { handleNotFound.teamNotFound(teamId) }
        verify { handleNotFound.eventNotFound(eventId) }
        verify { memberCreateParticipation.createParticipation(id, teamId, eventId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `addParticipation should log, call handleNotFound, not call memberAddParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = 1

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
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = 1

        val member = testMember(1).copy(id = id)

        every { handleNotFound.memberNotFound(id) } returns false
        every { handleNotFound.teamNotFound(teamId) } returns false
        every { handleNotFound.eventNotFound(eventId) } returns false
        every { handleNotFound.participationNotFound(teamId, id, eventId) } returns false
        every { memberDeleteParticipation.deleteParticipation(id, teamId, eventId) } returns member

        val response = memberController.removeParticipation(id, eventId, teamId)
        assert(output.contains(
            "Removing Participation:<Event:$eventId, Team:$teamId> from Member:$id"
        ))
        assertEquals(response, ResponseEntity.ok(member.toDto()))

        verify { handleNotFound.memberNotFound(id) }
        verify { handleNotFound.teamNotFound(teamId) }
        verify { handleNotFound.eventNotFound(eventId) }
        verify { handleNotFound.participationNotFound(teamId, id, eventId) }
        verify { memberDeleteParticipation.deleteParticipation(id, teamId, eventId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun `removeParticipation should log, call handleNotFound, not call memberDeleteParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val eventId = 1

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

    private fun testMember(index: Int = 0): Member {
        val lastnames = listOf("LUCAS", "THOMAZO", "DENIS")
        val firstnames = listOf("Claire", "Alexandre", "Ophélia")

        return Member(
            lastname = lastnames[index],
            firstname = firstnames[index],
            id = UUID.randomUUID(),
            contacts = emptySet(),
            participations = emptySet(),
            profilePictureLink = null
        )
    }
}