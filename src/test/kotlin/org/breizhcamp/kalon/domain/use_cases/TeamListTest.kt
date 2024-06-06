package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamList::class)
class TeamListTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamList: TeamList

    @Test
    fun `should call port to list teams`() {
        val team = generateRandomTeam()
        every { teamPort.list(TeamFilter.empty()) } returns listOf(team)

        assert(teamList.list(TeamFilter.empty()).contains(team))

        verify { teamPort.list(TeamFilter.empty()) }
    }
}