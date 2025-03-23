package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.TeamCreationReq
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamAdd::class)
class TeamAddTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamAdd: TeamAdd

    @Test
    fun `should call port to add team`() {
        val req = TeamCreationReq(name = "Orga")
        val team = generateRandomTeam().copy(name = req.name)

        every { teamPort.add(req) } returns team

        assertEquals(teamAdd.add(req), team)

        verify { teamPort.add(req) }
    }
}