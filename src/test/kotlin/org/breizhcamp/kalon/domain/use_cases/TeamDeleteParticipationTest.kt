package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Team
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
@WebMvcTest(TeamDeleteParticipation::class)
class TeamDeleteParticipationTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var teamDeleteParticipation: TeamDeleteParticipation

    private val testTeam = Team(
        id = UUID.randomUUID(),
        name = "Bureau",
        description = null,
        participations = emptySet()
    )

    private val memberId = UUID.randomUUID()
    private val eventId = 1

    @Test
    fun `should call port to remove participation`() {
        every { participationPort.removeByIds(testTeam.id, memberId, eventId) } just Runs
        every { teamPort.getById(testTeam.id) } returns Optional.of(testTeam)

        assertEquals(teamDeleteParticipation.removeParticipation(testTeam.id, memberId, eventId), testTeam)

        verify { participationPort.removeByIds(testTeam.id, memberId, eventId) }
        verify { teamPort.getById(testTeam.id) }
    }
}