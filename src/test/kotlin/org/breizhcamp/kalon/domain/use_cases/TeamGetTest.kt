package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should call port to get`(exists: Boolean) {
        val team = generateRandomTeam()
        every { teamPort.getById(team.id) } returns
                if (exists) Optional.of(team) else Optional.empty()

        val response = teamGet.getById(team.id)
        assertEquals(response.isPresent, exists)
        if (exists) {
            assertEquals(response.get(), team)
        }

        verify { teamPort.getById(team.id) }
    }
}