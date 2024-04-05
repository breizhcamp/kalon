package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
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
@WebMvcTest(TeamGet::class)
class TeamGetTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamGet: TeamGet

    private val testTeam = Team(
        id = UUID.randomUUID(),
        name = "Orga",
        description = null,
        participations = emptySet()
    )

    private val doesNotExistUUID = UUID.randomUUID()

    @Test
    fun `should call port to get`() {
        every { teamPort.getById(testTeam.id) } returns Optional.of(testTeam)
        every { teamPort.getById(doesNotExistUUID) } returns Optional.empty()

        assert(teamGet.getById(testTeam.id).isPresent)
        assertEquals(teamGet.getById(testTeam.id).get(), testTeam)
        assert(teamGet.getById(doesNotExistUUID).isEmpty)

        verify { teamPort.getById(testTeam.id) }
        verify { teamPort.getById(doesNotExistUUID) }
    }
}