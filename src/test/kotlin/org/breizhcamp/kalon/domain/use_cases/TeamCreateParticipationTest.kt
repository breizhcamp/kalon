package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamCreateParticipation::class)
class TeamCreateParticipationTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var teamCreateParticipation: TeamCreateParticipation

    private val id = UUID.randomUUID()
    private val memberId = UUID.randomUUID()
    private val eventId = 1

    private val testMember = Member(
        id = memberId,
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        participations = emptySet(),
        profilePictureLink = null,
    )

    private val testEvent = Event(
        id = eventId,
        name = null,
        year = 2024,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        eventParticipants = emptySet(),
        website = null,
    )

    private val testTeam = Team(
        id = id,
        name = "Bureau",
        description = null,
        participations = setOf(TeamParticipation(testMember, testEvent))
    )

    @Test
    fun `should call port to create participation`() {
        every { participationPort.createByIds(id, memberId, eventId) } just Runs
        every { teamPort.getById(id) } returns Optional.of(testTeam)

        assertEquals(teamCreateParticipation.createParticipation(id, memberId, eventId), testTeam)

        verify { participationPort.createByIds(id, memberId, eventId) }
        every { teamPort.getById(id) }
    }

}