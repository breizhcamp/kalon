package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.TeamCreationReq
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamAdd::class)
class TeamAddTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamAdd: TeamAdd

    private val testTeamCreationReq = TeamCreationReq(
        name = "Orga"
    )

    private val testTeam = Team(
        id = UUID.randomUUID(),
        name = testTeamCreationReq.name,
        description = null,
        participations = emptySet()
    )

    @Test
    fun `should call port to add team`() {
        every { teamPort.add(testTeamCreationReq) } returns testTeam

        assertEquals(teamAdd.add(testTeamCreationReq), testTeam)

        verify { teamPort.add(testTeamCreationReq) }
    }
}