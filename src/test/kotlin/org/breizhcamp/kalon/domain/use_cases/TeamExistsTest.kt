package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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

    private val existsUUID = UUID.randomUUID()
    private val doesNotExistUUID = UUID.randomUUID()

    @Test
    fun `should call port to test existence`() {
        every { teamPort.existsById(existsUUID) } returns true
        every { teamPort.existsById(doesNotExistUUID) } returns false

        assert(teamExists.exists(existsUUID))
        assert(!teamExists.exists(doesNotExistUUID))

        verify { teamPort.existsById(existsUUID) }
        verify { teamPort.existsById(doesNotExistUUID) }
    }
}