package org.breizhcamp.kalon.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.TeamCreationReq
import org.breizhcamp.kalon.application.dto.TeamDTO
import org.breizhcamp.kalon.application.dto.TeamPartialDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.*
import org.breizhcamp.kalon.domain.use_cases.*
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
@WebMvcTest(TeamController::class)
class TeamControllerTest {

    @MockkBean
    private lateinit var teamList: TeamList

    @MockkBean
    private lateinit var teamAdd: TeamAdd

    @MockkBean
    private lateinit var teamCreateParticipation: TeamCreateParticipation

    @MockkBean
    private lateinit var teamDeleteParticipation: TeamDeleteParticipation

    @MockkBean
    private lateinit var teamGet: TeamGet

    @MockkBean
    private lateinit var teamUpdate: TeamUpdate

    @MockkBean
    private lateinit var handleNotFound: HandleNotFound

    @Autowired
    private lateinit var teamController: TeamController

    @Test
    fun `list should log, call teamList with an empty filter and return a list of UUIDs`(
        output: CapturedOutput
    ) {
        val teams = listOf(0, 1, 2, 3).map(this::testTeam)
        every { teamList.list(TeamFilter.empty()) } returns teams

        assertEquals(teamController.list(), teams.map { it.id })
        assert(output.contains(
            "Listing all Teams by their IDs"
        ))

        verify { teamList.list(TeamFilter.empty()) }
    }

    @Test
    fun `addTeam should log, call teamAdd with the request and return the TeamDTO`(
        output: CapturedOutput
    ) {
        val name = "Comm"
        val request = TeamCreationReq(name)
        val created = testTeam().copy(name = name)
        every { teamAdd.add(request) } returns created

        assertEquals(teamController.addTeam(request), created.toDto())
        assert(output.contains(
            "Adding Team with values name=$name"
        ))

        verify { teamAdd.add(request) }
    }

    @Test
    fun `filter should log, call teamList with its filter and return a list of TeamDTOs`(
        output: CapturedOutput
    ) {
        val memberId = UUID.randomUUID()
        val filter = TeamFilter.empty().copy(memberId = memberId)
        val team = testTeam()

        every { teamList.list(filter) } returns listOf(team)

        assert(teamController.filter(filter).contains(team.toDto()))
        assert(output.contains(
            "Filtering Teams by memberId=${filter.memberId} eventId=${filter.eventId}"
        ))

        verify { teamList.list(filter) }
    }

    @Test
    fun `getTeamInfos should log, call handleNotFound and teamGet and return OK and the TeamDTO if Team found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val team = testTeam(1).copy(id = id)

        every { handleNotFound.teamNotFound(id) } returns false
        every { teamGet.getById(id) } returns Optional.of(team)

        assertEquals(teamController.getTeamInfos(id), ResponseEntity.ok(team.toDto()))
        assert(output.contains(
            "Retrieving Team:$id"
        ))

        verify { handleNotFound.teamNotFound(id) }
        verify { teamGet.getById(id) }
    }

    @Test
    fun `getTeamInfos should log, call handleNotFound, not call teamGet and return NOT_FOUND if Team not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()

        every { handleNotFound.teamNotFound(id) } returns true
        every { teamGet.getById(id) } returns Optional.empty()

        assertEquals(teamController.getTeamInfos(id), ResponseEntity<TeamDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Retrieving Team:$id"
        ))

        verify { handleNotFound.teamNotFound(id) }
        verify { teamGet.getById(id) wasNot Called }
    }

    @Test
    fun `update should log, call handleNotFound and teamUpdate and return OK and the TeamDTO if Team found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val name = "Matériel"
        val partial = TeamPartialDTO(name = name, description = null)
        val teamAfter = testTeam().copy(id = id, name = name)

        every { handleNotFound.teamNotFound(id) } returns false
        every { teamUpdate.update(id, partial.toObject()) } returns teamAfter

        assertEquals(teamController.update(id, partial), ResponseEntity.ok(teamAfter.toDto()))
        assert(output.contains(
            "Updating Team:$id with values name=${partial.name} description=${partial.description}"
        ))

        verify { handleNotFound.teamNotFound(id) }
        verify { teamUpdate.update(id, partial.toObject()) }
    }

    @Test
    fun `update should log, call handleNotFound, not call teamUpdate and return NOT_FOUND if Team not found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val partial = TeamPartialDTO(null, null)

        every { handleNotFound.teamNotFound(id) } returns true
        every { teamUpdate.update(id, partial.toObject()) }

        assertEquals(teamController.update(id, partial), ResponseEntity<TeamDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Updating Team:$id with values name=${partial.name} description=${partial.description}"
        ))

        verify { handleNotFound.teamNotFound(id) }
        verify { teamUpdate.update(id, partial.toObject()) wasNot Called }
    }

    @Test
    fun `addParticipant should log, call handleNotFound and teamAddParticipation and return OK and the TeamDTO if all found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = 1

        val member = Member(memberId, "LUCAS", "Claire", emptySet(), null, emptySet())
        val event = Event(eventId, null, 2020, null, null, null, null, null, null, null, emptySet())

        val team = testTeam(2).copy(id = id, participations = setOf(TeamParticipation(member, event)))

        every { handleNotFound.memberNotFound(memberId) } returns false
        every { handleNotFound.teamNotFound(id) } returns false
        every { handleNotFound.eventNotFound(eventId) } returns false
        every { teamCreateParticipation.createParticipation(id, memberId, eventId) } returns team

        assertEquals(teamController.addParticipant(id, memberId, eventId), ResponseEntity.ok(team.toDto()))
        assert(output.contains(
            "Adding Participation:<Member:$memberId, Event:$eventId> to Team:$id"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify { handleNotFound.teamNotFound(id) }
        verify { handleNotFound.eventNotFound(eventId) }
        verify { teamCreateParticipation.createParticipation(id, memberId, eventId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `addParticipant should log, call handleNotFound, not call teamAddParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = 1

        every { handleNotFound.memberNotFound(memberId) } returns (index==0)
        every { handleNotFound.teamNotFound(id) } returns (index==1)
        every { handleNotFound.eventNotFound(eventId) } returns (index==2)
        every { teamCreateParticipation.createParticipation(id, memberId, eventId) }

        assertEquals(teamController.addParticipant(id, memberId, eventId), ResponseEntity<TeamDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Adding Participation:<Member:$memberId, Event:$eventId> to Team:$id"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(id) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(eventId) }
        verify { teamCreateParticipation.createParticipation(id, memberId, eventId) wasNot Called }
    }

    @Test
    fun `deleteParticipant should log, call handleNotFound and teamDeleteParticipation and return OK and the TeamDTO if all found`(
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = 1

        val team = testTeam(3).copy(id = id)

        every { handleNotFound.memberNotFound(memberId) } returns false
        every { handleNotFound.teamNotFound(id) } returns false
        every { handleNotFound.eventNotFound(eventId) } returns false
        every { handleNotFound.participationNotFound(id, memberId, eventId) } returns false
        every { teamDeleteParticipation.removeParticipation(id, memberId, eventId) } returns team

        assertEquals(teamController.removeParticipant(id, memberId, eventId), ResponseEntity.ok(team.toDto()))
        assert(output.contains(
            "Removing Participant:<Member:$memberId, Event:$eventId> from Team:$id"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify { handleNotFound.teamNotFound(id) }
        verify { handleNotFound.eventNotFound(eventId) }
        verify { handleNotFound.participationNotFound(id, memberId, eventId) }
        verify { teamDeleteParticipation.removeParticipation(id, memberId, eventId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun `removeParticipant should log, call handleNotFound, not call teamDeleteParticipation and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = 1

        every { handleNotFound.memberNotFound(memberId) } returns (index==0)
        every { handleNotFound.teamNotFound(id) } returns (index==1)
        every { handleNotFound.eventNotFound(eventId) } returns (index==2)
        every { handleNotFound.participationNotFound(id, memberId, eventId) } returns (index==3)
        every { teamDeleteParticipation.removeParticipation(id, memberId, eventId) }

        assertEquals(teamController.removeParticipant(id, memberId, eventId), ResponseEntity<TeamDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Removing Participant:<Member:$memberId, Event:$eventId> from Team:$id"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(id) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(eventId) }
        verify(exactly = if (index <= 2) 0 else 1) { handleNotFound.participationNotFound(id, memberId, eventId) }
        verify { teamDeleteParticipation.removeParticipation(id, memberId, eventId) wasNot Called }
    }

    private fun testTeam(index: Int = 0): Team {
        val names = listOf("Orga", "Bureau", "Développement", "Anciens membres du bureau")
        val i: Int = if (index > 3) 0 else index

        return Team(
            UUID.randomUUID(),
            names[i],
            null,
            emptySet()
        )
    }
}