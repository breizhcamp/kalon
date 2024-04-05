package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamUpdate::class)
class TeamUpdateTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamUpdate: TeamUpdate

    private val testTeamBefore = Team(
        id = UUID.randomUUID(),
        name = "Orga",
        description = null,
        participations = emptySet()
    )

    private val testTeamPartial = TeamPartial(
        description = "Équipe responsable de l'organisation des événements",
        name = null
    )

    private val testTeamAfter = testTeamBefore.copy(description = testTeamPartial.description)

    @Test
    fun `should call port to update`() {
        every { teamPort.update(testTeamBefore.id, testTeamPartial) } returns testTeamAfter

        assertEquals(teamUpdate.update(testTeamBefore.id, testTeamPartial), testTeamAfter)

        verify { teamPort.update(testTeamBefore.id, testTeamPartial) }
    }
}