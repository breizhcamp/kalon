package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.breizhcamp.kalon.testUtils.generateRandomTeam
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

    @Test
    fun `should call port to create participation`() {
        val teamBefore = generateRandomTeam()

        val member = generateRandomMember()
        val event = generateRandomEvent()
        val participation = TeamParticipation(member, event)
        val teamAfter = teamBefore.copy(participations = setOf(participation))

        every { participationPort.createByIds(teamBefore.id, member.id, event.id) } just Runs
        every { teamPort.getById(teamBefore.id) } returns Optional.of(teamAfter)

        assertEquals(teamCreateParticipation.createParticipation(teamBefore.id, member.id, event.id), teamAfter)

        verify { participationPort.createByIds(teamBefore.id, member.id, event.id) }
        every { teamPort.getById(teamBefore.id) }
    }

}