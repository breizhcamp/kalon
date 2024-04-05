package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamList::class)
class TeamListTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamList: TeamList

    private val testTeam = Team(
        id = UUID.randomUUID(),
        name = "Orga",
        description = null,
        participations = emptySet()
    )

    @Test
    fun `should call port to list teams`() {
        every { teamPort.list(TeamFilter.empty()) } returns listOf(testTeam)

        assert(teamList.list(TeamFilter.empty()).contains(testTeam))

        verify { teamPort.list(TeamFilter.empty()) }
    }
}