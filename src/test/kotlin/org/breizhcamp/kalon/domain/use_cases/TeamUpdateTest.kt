package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamUpdate::class)
class TeamUpdateTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamUpdate: TeamUpdate

    @Test
    fun `should call port to update`() {
        val partial = TeamPartial(name = generateRandomHexString(), description = null)
        val team = generateRandomTeam().copy(name = partial.name!!)
        every { teamPort.update(team.id, partial) } returns team

        assertEquals(teamUpdate.update(team.id, partial), team)

        verify { teamPort.update(team.id, partial) }
    }
}