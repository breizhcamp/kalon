package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamDeleteParticipation::class)
class TeamDeleteParticipationTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var teamDeleteParticipation: TeamDeleteParticipation

    @Test
    fun `should call port to remove participation`() {
        val team = generateRandomTeam()
        val memberId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        every { participationPort.removeByIds(team.id, memberId, eventId) } just Runs
        every { teamPort.getById(team.id) } returns Optional.of(team)

        assertEquals(teamDeleteParticipation.removeParticipation(team.id, memberId, eventId), team)

        verify { participationPort.removeByIds(team.id, memberId, eventId) }
        verify { teamPort.getById(team.id) }
    }
}