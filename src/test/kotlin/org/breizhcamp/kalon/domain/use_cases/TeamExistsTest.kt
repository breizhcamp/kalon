package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamExists::class)
class TeamExistsTest {

    @MockkBean
    private lateinit var teamPort: TeamPort

    @Autowired
    private lateinit var teamExists: TeamExists

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should call port to test existence`(exists: Boolean) {
        val id = UUID.randomUUID()
        every { teamPort.existsById(id) } returns exists

        assertEquals(teamExists.exists(id), exists)

        verify { teamPort.existsById(id) }
    }
}