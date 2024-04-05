package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberParticipation
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberCreateParticipation::class)
class MemberCreateParticipationTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var memberCreateParticipation: MemberCreateParticipation

    private val id = UUID.randomUUID()
    private val teamId = UUID.randomUUID()
    private val eventId = 1

    private val testTeam = Team(
        id = teamId,
        name = "Développement",
        description = null,
        participations = emptySet(),
    )

    private val testEvent = Event(
        id = eventId,
        name = "Breizh camp 2024",
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

    private val testMember = Member(
        id = id,
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        profilePictureLink = null,
        participations = setOf(MemberParticipation(testTeam, testEvent))
    )

    @Test
    fun `should call port to create participation`() {
        every { participationPort.createByIds(teamId, id, eventId) } just Runs
        every { memberPort.getById(id) }returns Optional.of(testMember)

        assertEquals(memberCreateParticipation.createParticipation(id, teamId, eventId), testMember)

        verify { participationPort.createByIds(teamId, id, eventId) }
        verify { memberPort.getById(id) }
    }
}